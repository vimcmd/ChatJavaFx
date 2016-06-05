package com.github.vimcmd.server;

import java.io.PrintStream;

public interface ChatServerClientConnectionRunnable extends Runnable {

    PrintStream getPrintStream();

}
