package com.github.vimcmd.chatJavaFx.server.commands;

import com.github.vimcmd.chatJavaFx.server.ServerResourceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ServerComandlet {
    COMMAND_SIGN(ServerResourceManager.SERVER_COMMAND_CHARACTER),
    COMMAND_UNKNOWN(ServerResourceManager.SERVER_COMMAND_UNKNOWN),
    REGISTER(ServerResourceManager.SERVER_COMMAND_REGISTER),
    TO_RECIPIENT(ServerResourceManager.RECIPIENT_CHARACTER),
    TIME(ServerResourceManager.SERVER_COMMAND_TIME),
    HELP(ServerResourceManager.SERVER_COMMAND_HELP);

    private String value;

    private ServerComandlet(String value) {
        this.value = value;
    }

    /**
     * Extracts commands from message text
     *
     * @param text message
     * @return Map, where K - comandlet object, V - command arguments list
     */
    public static Map<ServerComandlet, List<String>> extractCommands(String text) {
        Map<ServerComandlet, List<String>> messageCommands = new HashMap<>();

        Pattern pattern = Pattern.compile("[" + TO_RECIPIENT.toString() + "|" + COMMAND_SIGN.toString() + "][a-zA-Z_0-9:]+\\b");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            if (matcher.group().startsWith(TO_RECIPIENT.toString())) {
                List<String> recipientList = messageCommands.get(TO_RECIPIENT);
                if (recipientList == null) {
                    recipientList = new ArrayList<>();
                }
                String recipient = matcher.group().replaceAll(TO_RECIPIENT.toString(), "");
                recipientList.add(recipient);
                messageCommands.put(TO_RECIPIENT, recipientList);

            }

            if (matcher.group().startsWith(COMMAND_SIGN.toString())) {
                // commandWithArgumentsArray[] = {String command, String arguments}
                String[] commandWithArgumentsArray = matcher.group()
                                                            .split(ServerResourceManager.SERVER_COMMAND_ARGUMENT_SEPARATOR, 2);

                ServerComandlet command = getComandletByString(commandWithArgumentsArray[0]);
                List<String> commandArgumentList = messageCommands.get(command);
                if (commandArgumentList == null) {
                    commandArgumentList = new ArrayList<>();
                }

                if (command != COMMAND_UNKNOWN) {
                    if (commandWithArgumentsArray.length > 1) {
                        commandArgumentList.add(commandWithArgumentsArray[1]); // add arguments
                    }
                    messageCommands.put(command, commandArgumentList);
                } else {
                    commandArgumentList.add(commandWithArgumentsArray[0]); // add unknown command
                    messageCommands.put(command, commandArgumentList);
                }
            }
        }

        return messageCommands;
    }

    private static ServerComandlet getComandletByString(String stringCommand) {
        for(ServerComandlet comandlet : ServerComandlet.values()) {
            if (comandlet.toString().equals(stringCommand)) {
                return comandlet;
            }
        }
        return COMMAND_UNKNOWN;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
