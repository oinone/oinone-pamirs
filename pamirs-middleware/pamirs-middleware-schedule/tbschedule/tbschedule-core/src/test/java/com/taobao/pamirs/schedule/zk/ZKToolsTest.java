package com.taobao.pamirs.schedule.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ZKToolsTest {

    private static ZooKeeper zooKeeper;

    @BeforeClass
    public static void setUp() throws IOException {
        zooKeeper = new ZooKeeper("localhost:2181", 3000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event);
            }
        });
    }

    @AfterClass
    public static void setDown() throws InterruptedException {
        if (null != zooKeeper) {
            zooKeeper.close();
        }
    }

    @Test
    public void createPath() {
    }

    @Test
    public void printTree() {
    }

    @Test
    public void deleteTree() {
    }

    @Test
    public void getTree() throws Exception {
        String[] paths = ZKTools.getTree(zooKeeper, "/schedule");
        for (int i = paths.length -1; i >= 0; i--) {
            System.out.println(paths[i]);
        }
    }
}