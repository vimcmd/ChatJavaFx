package com.github.vimcmd.chatJavaFx.server.connectors.senderConnector;

import com.github.vimcmd.chatJavaFx.message.ChatMessage;
import com.github.vimcmd.chatJavaFx.server.socketThread.ChatServerClientConnectionRunnable;

public interface ChatServerSenderConnector {

    void sendBroadcastMessage(ChatMessage message);

    void sendPrivateMessage(ChatMessage message);

    void sendServerPrivateMessage(String recipient, String messageBody);

    void sendServerPrivateMessage(ChatServerClientConnectionRunnable client, String messageBody);

    void sendServerBroadcastMessage(String messageBody);

}
