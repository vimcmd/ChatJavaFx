package com.github.vimcmd.server.commands;

import com.github.vimcmd.server.resources.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Comandlet {
    COMMAND_SIGN(ResourceManager.SERVER_COMMAND_CHARACTER),
    COMMAND_UNKNOWN(ResourceManager.SERVER_COMMAND_UNKNOWN),
    REGISTER(ResourceManager.SERVER_COMMAND_REGISTER),
    TO_RECIPIENT(ResourceManager.RECIPIENT_CHARACTER),
    TIME(ResourceManager.SERVER_COMMAND_TIME),
    HELP(ResourceManager.SERVER_COMMAND_HELP);

    private String value;

    private Comandlet(String value) {
        this.value = value;
    }

    /**
     * Extracts commands from message text
     *
     * @param text message
     * @return Map, where K - comandlet object, V - command arguments list
     */
    public static Map<Comandlet, List<String>> extractCommands(String text) {
        Map<Comandlet, List<String>> messageCommands = new HashMap<>();

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
                                                            .split(ResourceManager.SERVER_COMMAND_ARGUMENT_SEPARATOR, 2);

                Comandlet command = getComandletByString(commandWithArgumentsArray[0]);
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

    private static Comandlet getComandletByString(String stringCommand) {
        for(Comandlet comandlet : Comandlet.values()) {
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
