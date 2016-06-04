package java.com.github.vimcmd.connector;

import java.com.github.vimcmd.message.ChatMessage;
import java.com.github.vimcmd.server.ChatServerClientThread;

public interface ChatServerSenderConnector {

    void sendBroadcastMessage(ChatMessage message);

    void sendPrivateMessage(ChatMessage message);

    void sendServerPrivateMessage(String recipient, String messageBody);

    void sendServerPrivateMessage(ChatServerClientThread client, String messageBody);

    void sendServerBroadcastMessage(String messageBody);

}
