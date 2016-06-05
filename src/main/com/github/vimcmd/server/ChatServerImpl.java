package com.github.vimcmd.server;

import com.github.vimcmd.message.ChatMessage;
import com.github.vimcmd.message.ChatMessageImpl;
import com.github.vimcmd.server.commands.Comandlet;
import com.github.vimcmd.server.connectors.senderConnector.ChatServerSenderConnector;
import com.github.vimcmd.server.connectors.senderConnector.ChatServerSenderImpl;
import com.github.vimcmd.server.resources.ResourceManager;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServerImpl implements ChatServer {
    static final int MAX_LOGIN_LENGTH = 20;
    private final Set<Comandlet> supportedCommands = new HashSet<Comandlet>() {
        {
            this.add(Comandlet.COMMAND_UNKNOWN);
            this.add(Comandlet.REGISTER);
            this.add(Comandlet.TIME);
            this.add(Comandlet.TO_RECIPIENT);
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

            System.out.println(String.format(ResourceManager.SERVER_STATUS_INITIALIZED, port));

            for(; ; ) {
                System.out.println(ResourceManager.SERVER_STATUS_WAITING_CLIENTS);
                Socket socket = serverSocket.accept();
                System.out.println(String.format(ResourceManager.SERVER_USER_CONNECTED, socket.getInetAddress()
                                                                                              .getHostName()));
                Thread clientThread = new Thread(new ChatServerClientConnectionImpl(this, socket));
                clientThread.start();

                //if (clientThread.isErrorsOccured()) {
                //    clientThread.interrupt();
                //}

            }

        } catch (BindException e) {
            System.err.println(ResourceManager.SERVER_STATUS_ADDRESS_RESERVATION_ERROR + e);
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
        Map<Comandlet, List<String>> messageCommands = Comandlet.extractCommands(messageBody);

        updateFromFieldIfUserRegistered(from, message);

        processCommands(from, message, messageCommands);

        if (message.getFrom().isEmpty() && !messageCommands.containsKey(Comandlet.REGISTER)) {
            sender.sendServerPrivateMessage(from, String.format(ResourceManager.SERVER_USER_MUST_REGISTER, Comandlet.REGISTER
                    .toString() + ResourceManager.SERVER_COMMAND_ARGUMENT_SEPARATOR));
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

    private void processCommands(ChatServerClientConnectionRunnable from, ChatMessage message, Map<Comandlet, List<String>> messageCommands) {
        for(Map.Entry<Comandlet, List<String>> messageCommand : messageCommands.entrySet()) {

            Comandlet messageKey = messageCommand.getKey();
            List<String> messageValue = messageCommand.getValue();

            if (supportedCommands.contains(messageKey)) {

                // TODO: 04.06.2016 take commands from supportedCommands
                if (messageKey.equals(Comandlet.REGISTER)) {
                    String userNameForRegistration = messageValue.get(0);
                    if (userNameForRegistration != null || !userNameForRegistration.isEmpty()) {
                        // TODO: 04.06.2016 check if user exists, and get it name
                        registerUser(from, messageValue.get(0));
                        message.setFrom(userNameForRegistration);
                    }
                }

                if (messageKey.equals(Comandlet.TO_RECIPIENT)) {
                    message.addRecipients(messageValue);
                }

                if (messageKey.equals(Comandlet.TIME)) {
                    sender.sendServerPrivateMessage(from, new Date().toString());
                }

                if (messageKey.equals(Comandlet.COMMAND_UNKNOWN)) {
                    final List<String> unknownCommands = messageCommands.get(Comandlet.COMMAND_UNKNOWN);
                    if (unknownCommands != null) {
                        sender.sendServerPrivateMessage(from, String.format(ResourceManager.SERVER_COMMAND_UNKNOWN_MESSAGE, unknownCommands));
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
            sender.sendServerPrivateMessage(ResourceManager.SERVER_USER_EXISTS, name);
        }

        if (isUserNameCorrect(name)) {
            isCorrect = true;
        } else {
            sender.sendServerPrivateMessage(client, String.format(ResourceManager.SERVER_USER_NAME_INCORRECT, name));
        }

        if (isFree && isCorrect) {
            users.put(name, client);
            sendSuccessRegistrationMessage(name);
            return true;
        }

        return false;
    }

    private void sendSuccessRegistrationMessage(String name) {
        sender.sendServerBroadcastMessage(String.format(ResourceManager.SERVER_USER_SUCCESSFULLY_REGISTERED, name));
        System.out.println(name + " registered");
        sender.sendServerPrivateMessage(name, String.format(ResourceManager.SERVER_USER_WELCOME_MESSAGE, name));
        sender.sendServerPrivateMessage(name, ResourceManager.SERVER_TIPS_PRIVATE);
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
