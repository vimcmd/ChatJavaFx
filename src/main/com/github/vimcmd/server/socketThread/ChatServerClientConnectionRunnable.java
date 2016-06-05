package com.github.vimcmd.server.socketThread;

import java.io.PrintStream;

public interface ChatServerClientConnectionRunnable extends Runnable {

    PrintStream getPrintStream();

}
