package com.github.vimcmd.server;

import com.github.vimcmd.server.socketThread.ChatServerClientConnectionRunnable;

import java.net.InetAddress;
import java.util.Map;

public interface ChatServer {

    Map<String, ChatServerClientConnectionRunnable> getUsers();

    boolean isUserExists(String userName);

    ChatServerClientConnectionRunnable getUser(String userName);

    void send(ChatServerClientConnectionRunnable from, String messageBody);

    InetAddress getInetAddress();

    int getPortNumber();
}
