package java.com.github.vimcmd.server;

import java.util.Map;

public interface ChatServer {

    Map<String, ChatServerClientThread> getUsers();

    boolean isUserExists(String userName);

    ChatServerClientThread getUser(String userName);

    void send(ChatServerClientThread from, String messageBody);

}
