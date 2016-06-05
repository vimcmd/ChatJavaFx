package com.github.vimcmd.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ChatClientImpl implements ChatClient {

    Socket socket = null;
    BufferedReader reader = null;
    PrintStream printStream = null;

    public ChatClientImpl(InetAddress host, int port) {
        try {
            socket = new Socket(host, port);
            printStream = new PrintStream(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.send("anonymous message #everyone");
            this.send("#register:Luke");
            this.send("here i am");
            this.send("take look #awesomeCommand #anotherCommand");
            this.send("take look #time");

            for(; ; ) {
                System.out.println(reader.readLine());
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            System.err.println("Connection lost with " + host.getHostName() + ": " + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // TODO: 05.06.2016 remove main
        try {
            ChatClientImpl client = new ChatClientImpl(InetAddress.getLocalHost(), 54321);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    private void send(String message) {
        printStream.println(message);
    }
}
