package com.github.vimcmd.chatJavaFx.server;

import com.github.vimcmd.chatJavaFx.message.ChatMessageImpl;
import com.github.vimcmd.chatJavaFx.server.connectors.senderConnector.ChatServerSenderImpl;
import com.github.vimcmd.chatJavaFx.server.socketThread.ChatServerClientConnectionImpl;
import com.github.vimcmd.chatJavaFx.message.ChatMessage;
import com.github.vimcmd.chatJavaFx.server.commands.ServerComandlet;
import com.github.vimcmd.chatJavaFx.server.connectors.senderConnector.ChatServerSenderConnector;
import com.github.vimcmd.chatJavaFx.server.socketThread.ChatServerClientConnectionRunnable;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServerImpl implements ChatServer {
    static final int MAX_LOGIN_LENGTH = 20;
    private final Set<ServerComandlet> supportedCommands = new HashSet<ServerComandlet>() {
        {
            this.add(ServerComandlet.COMMAND_UNKNOWN);
            this.add(ServerComandlet.REGISTER);
            this.add(ServerComandlet.TIME);
            this.add(ServerComandlet.TO_RECIPIENT);
        }
    };
    private Map<String, ChatServerClientConnectionRunnable> users;
    private ChatServerSenderConnector sender;
    private ServerSocket serverSocket = null;
    private int port = 54321;

    public ChatServerImpl(int port) {
        this.port = port; // FIXME: 05.06.2016 check intervals
        users = new HashMap<>();
        sender = new ChatServerSenderImpl(this);
        // TODO: 04.06.2016 create and run server bot thread

        try {
            serverSocket = new ServerSocket(port);

            System.out.println(String.format(ServerResourceManager.SERVER_STATUS_INITIALIZED, port));

            for(; ; ) {
                System.out.println(ServerResourceManager.SERVER_STATUS_WAITING_CLIENTS);
                Socket socket = serverSocket.accept();
                System.out.println(String.format(ServerResourceManager.SERVER_USER_CONNECTED, socket.getInetAddress()
                                                                                                    .getHostName()));
                Thread clientThread = new Thread(new ChatServerClientConnectionImpl(this, socket));
                clientThread.start();

                //if (clientThread.isErrorsOccured()) {
                //    clientThread.interrupt();
                //}

            }

        } catch (BindException e) {
            System.err.println(ServerResourceManager.SERVER_STATUS_ADDRESS_RESERVATION_ERROR + e);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<String, ChatServerClientConnectionRunnable> getUsers() {
        // TODO: 04.06.2016 remove or make private
        return users;
    }

    @Override
    public boolean isUserExists(String userName) {
        return users.containsKey(userName);
    }

    @Override
    public ChatServerClientConnectionRunnable getUser(String userName) {
        if (isUserExists(userName)) {
            return users.get(userName);
        }
        // FIXME: 04.06.2016 return anonymous
        return null;
    }

    @Override
    public void send(ChatServerClientConnectionRunnable from, String messageBody) {
        ChatMessage message = new ChatMessageImpl(from, messageBody);
        Map<ServerComandlet, List<String>> messageCommands = ServerComandlet.extractCommands(messageBody);

        updateFromFieldIfUserRegistered(from, message);

        processCommands(from, message, messageCommands);

        if (message.getFrom().isEmpty() && !messageCommands.containsKey(ServerComandlet.REGISTER)) {
            sender.sendServerPrivateMessage(from, String.format(ServerResourceManager.SERVER_USER_MUST_REGISTER, ServerComandlet.REGISTER
                    .toString() + ServerResourceManager.SERVER_COMMAND_ARGUMENT_SEPARATOR));
        } else {
            if (message.getRecipients().size() <= 0) {
                sender.sendBroadcastMessage(message);
            } else {
                sender.sendPrivateMessage(message);
            }
        }

    }

    @Override
    public InetAddress getInetAddress() {
        return serverSocket.getInetAddress();
    }

    @Override
    public int getPortNumber() {
        return this.port;
    }

    private void processCommands(ChatServerClientConnectionRunnable from, ChatMessage message, Map<ServerComandlet, List<String>> messageCommands) {
        for(Map.Entry<ServerComandlet, List<String>> messageCommand : messageCommands.entrySet()) {

            ServerComandlet messageKey = messageCommand.getKey();
            List<String> messageValue = messageCommand.getValue();

            if (supportedCommands.contains(messageKey)) {

                // TODO: 04.06.2016 take commands from supportedCommands
                if (messageKey.equals(ServerComandlet.REGISTER)) {
                    String userNameForRegistration = messageValue.get(0);
                    if (userNameForRegistration != null || !userNameForRegistration.isEmpty()) {
                        // TODO: 04.06.2016 check if user exists, and get it name
                        registerUser(from, messageValue.get(0));
                        message.setFrom(userNameForRegistration);
                    }
                }

                if (messageKey.equals(ServerComandlet.TO_RECIPIENT)) {
                    message.addRecipients(messageValue);
                }

                if (messageKey.equals(ServerComandlet.TIME)) {
                    sender.sendServerPrivateMessage(from, new Date().toString());
                }

                if (messageKey.equals(ServerComandlet.COMMAND_UNKNOWN)) {
                    final List<String> unknownCommands = messageCommands.get(ServerComandlet.COMMAND_UNKNOWN);
                    if (unknownCommands != null) {
                        sender.sendServerPrivateMessage(from, String.format(ServerResourceManager.SERVER_COMMAND_UNKNOWN_MESSAGE, unknownCommands));
                    }
                }

            }
        }
    }

    private boolean registerUser(ChatServerClientConnectionRunnable client, String name) {
        boolean isFree = false;
        boolean isCorrect = false;

        if (users.get(name) == null) {
            isFree = true;
        } else {
            sender.sendServerPrivateMessage(ServerResourceManager.SERVER_USER_EXISTS, name);
        }

        if (isUserNameCorrect(name)) {
            isCorrect = true;
        } else {
            sender.sendServerPrivateMessage(client, String.format(ServerResourceManager.SERVER_USER_NAME_INCORRECT, name));
        }

        if (isFree && isCorrect) {
            users.put(name, client);
            sendSuccessRegistrationMessage(name);
            return true;
        }

        return false;
    }

    private void sendSuccessRegistrationMessage(String name) {
        sender.sendServerBroadcastMessage(String.format(ServerResourceManager.SERVER_USER_SUCCESSFULLY_REGISTERED, name));
        System.out.println(name + " registered");
        sender.sendServerPrivateMessage(name, String.format(ServerResourceManager.SERVER_USER_WELCOME_MESSAGE, name));
        sender.sendServerPrivateMessage(name, ServerResourceManager.SERVER_TIPS_PRIVATE);
    }

    private boolean isUserNameCorrect(String name) {
        return !( name.length() > MAX_LOGIN_LENGTH || name.contains(" ") );
    }

    private void updateFromFieldIfUserRegistered(ChatServerClientConnectionRunnable from, ChatMessage message) {
        for(Map.Entry<String, ChatServerClientConnectionRunnable> userEntry : users.entrySet()) {
            if (from.equals(userEntry.getValue())) {
                message.setFrom(userEntry.getKey());
                break;
            }
        }
    }
}
