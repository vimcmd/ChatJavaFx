package com.github.vimcmd.server.socketThread;

import com.github.vimcmd.server.ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class ChatServerClientConnectionImpl implements ChatServerClientConnectionRunnable {

    private PrintStream printStream;
    private BufferedReader readerStream;
    private InetAddress inetAddress;
    private ChatServer server;
    private Socket socket;

    public ChatServerClientConnectionImpl(ChatServer server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        printStream = new PrintStream(socket.getOutputStream());
        readerStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        inetAddress = socket.getInetAddress();
    }

    @Override
    public PrintStream getPrintStream() {
        return printStream;
    }

    @Override
    public void run() {
        try {
            for(; ; ) {
                // TODO: 02.06.2016 before read - thread.sleep to prevent ddos :)
                String message = readerStream.readLine();
                // TODO: 01.06.2016 prepare message here, not on server?
                server.send(this, message);

                if (Thread.interrupted()) {
                    return; // stop runnable
                }

            }

        } catch (SocketException e) {
            System.err.println("Connection ended: " + e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private void disconnect() {
        if (printStream != null) {
            printStream.close();
        }
        if (readerStream != null) {
            try {
                readerStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //System.out.println(userLoginName + " disconnected: " + unregistered);
    }
}
