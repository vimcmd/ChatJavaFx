package com.github.vimcmd.chatJavaFx.server.socketThread;

import java.io.PrintStream;

public interface ChatServerClientConnectionRunnable extends Runnable {

    PrintStream getPrintStream();

}
