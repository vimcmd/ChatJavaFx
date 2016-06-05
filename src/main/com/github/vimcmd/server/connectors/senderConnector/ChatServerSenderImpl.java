package com.github.vimcmd.server.connectors.senderConnector;

import com.github.vimcmd.server.ChatServer;
import com.github.vimcmd.message.ChatMessage;
import com.github.vimcmd.server.ChatServerClientConnectionRunnable;
import com.github.vimcmd.server.resources.ResourceManager;
import java.io.PrintStream;
import java.util.Map;

public class ChatServerSenderImpl implements ChatServerSenderConnector {
    private ChatServer server;

    public ChatServerSenderImpl(ChatServer server) {
        this.server = server;
    }

    @Override
    public void sendBroadcastMessage(ChatMessage message) {
        String formattedMessage = formatBroadcastMessage(message);

        for(Map.Entry<String, ChatServerClientConnectionRunnable> user : server.getUsers().entrySet()) {
            final PrintStream userPrintStream = user.getValue().getPrintStream();
            userPrintStream.println(formattedMessage);
            userPrintStream.flush();
        }

    }

    @Override
    public void sendPrivateMessage(ChatMessage message) {
        for(String recipient : message.getRecipients()) {
            if (server.isUserExists(recipient)) {
                final PrintStream recipientPrintStream = server.getUser(recipient).getPrintStream();
                final PrintStream senderPrintStream = server.getUser(message.getFrom()).getPrintStream();

                if (recipientPrintStream != null && senderPrintStream != null) {
                    recipientPrintStream.println(formatPrivateMessage(message));
                    recipientPrintStream.flush();
                    senderPrintStream.println(formatPrivateMessage(message));
                    senderPrintStream.flush();
                }

            } else {
                sendServerPrivateMessage(message.getFrom(), String.format(ResourceManager.SERVER_USER_NOT_REGISTERED, recipient));
            }
        }
    }

    @Override
    public void sendServerBroadcastMessage(String messageBody) {
        String formattedServerMessage = formatServerMessage(messageBody);

    }

    @Override
    public void sendServerPrivateMessage(String recipient, String messageBody) {
        if (server.isUserExists(recipient)) {
            final PrintStream recipientPrintStream = server.getUser(recipient).getPrintStream();
            recipientPrintStream.println(formatServerMessage(messageBody));
            recipientPrintStream.flush();
        }
    }

    @Override
    public void sendServerPrivateMessage(ChatServerClientConnectionRunnable recipient, String messageBody) {
        final PrintStream recipientPrintStream = recipient.getPrintStream();
        recipientPrintStream.println(formatServerMessage(messageBody));
        recipientPrintStream.flush();
    }

    private String formatBroadcastMessage(ChatMessage message) {
        // TODO: 31.05.2016 format from properties
        return message.getFrom() + ": " + message.getMessageBody();
    }

    private String formatPrivateMessage(ChatMessage message) {
        // TODO: 31.05.2016 format from properties
        // TODO: 31.05.2016 display only one recipient, not array (or display few, not all / display [private])
        return message.getFrom() + " " + ResourceManager.RECIPIENT_CHARACTER + message.getRecipients() + ": " + message.getMessageBody();
    }

    private String formatServerMessage(String messageBody) {
        // TODO: 31.05.2016 format from properties
        //return "SERVER: " + MessageParser.parseMessage(message);
        return "SERVER: " + messageBody;
    }
}
