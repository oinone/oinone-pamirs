package com.taobao.pamirs.schedule.test;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.curator.test.TestingServer;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmbedTestingServer {

    private static final int PORT = 2181;

    private static volatile TestingServer testingServer;

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
