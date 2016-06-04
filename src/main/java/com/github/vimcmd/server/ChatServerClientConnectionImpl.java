package java.com.github.vimcmd.server;

import java.io.PrintStream;
import java.net.Socket;

public class ChatServerClientConnectionImpl implements ChatServerClientConnectionRunnable {

    public ChatServerClientConnectionImpl(ChatServer chatServer, Socket socket) {

    }

    @Override
    public PrintStream getPrintStream() {
        return null;
    }

    @Override
    public void run() {

    }
}
