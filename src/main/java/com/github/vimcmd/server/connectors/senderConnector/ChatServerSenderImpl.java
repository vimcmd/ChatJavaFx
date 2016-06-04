package java.com.github.vimcmd.server.connectors.senderConnector;

import java.com.github.vimcmd.message.ChatMessage;
import java.com.github.vimcmd.server.ChatServer;
import java.com.github.vimcmd.server.ChatServerClientConnectionRunnable;

public class ChatServerSenderImpl implements ChatServerSenderConnector {
    public ChatServerSenderImpl(ChatServer chatServer) {

    }

    @Override
    public void sendBroadcastMessage(ChatMessage message) {

    }

    @Override
    public void sendPrivateMessage(ChatMessage message) {

    }

    @Override
    public void sendServerPrivateMessage(String recipient, String messageBody) {

    }

    @Override
    public void sendServerPrivateMessage(ChatServerClientConnectionRunnable client, String messageBody) {

    }

    @Override
    public void sendServerBroadcastMessage(String messageBody) {

    }
}
