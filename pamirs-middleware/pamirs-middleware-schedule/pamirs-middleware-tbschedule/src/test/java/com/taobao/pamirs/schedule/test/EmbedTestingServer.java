package com.taobao.pamirs.schedule.test;

import org.apache.curator.test.TestingServer;

import java.io.IOException;

public final class EmbedTestingServer {

    private static final int PORT = 2181;

    private static volatile TestingServer testingServer;

    private EmbedTestingServer() {
    }

    public static void start() {
        if (null != testingServer) {
            return;
        }
        try {
            testingServer = new TestingServer(PORT, true);
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    try {
                        testingServer.close();
                    } catch (final IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }
}
