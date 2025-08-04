//package pro.shushi.pamirs.middle.lock.zookeeper;
//
//import org.apache.curator.RetryPolicy;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.CuratorFrameworkFactory;
//import org.apache.curator.framework.recipes.locks.InterProcessMutex;
//import org.apache.curator.framework.state.ConnectionState;
//import org.apache.curator.framework.state.ConnectionStateListener;
//import org.apache.curator.retry.ExponentialBackoffRetry;
//import org.apache.zookeeper.CreateMode;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import pro.shushi.pamirs.middleware.zookeeper.config.ZookeeperProperties;
//
//import jakarta.annotation.PreDestroy;
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class CuratorUtils2 implements InitializingBean {
//
//    @Autowired
//    private ZookeeperProperties zookeeperProperties;
//
//    private static final Logger log = LoggerFactory.getLogger(CuratorUtils2.class);
//
//    private static CuratorFramework client;
//
//    private static final String LOCK_PATH = "/lock/";
//
//    private static long lockTimeoutMs = 90000;
//
//    public static InterProcessMutex tryLock(String path, int waitSeconds) {
//        SessionConnectionListener sessionConnectionListener = new SessionConnectionListener(path, waitSeconds);
//        client.getConnectionStateListenable().addListener(sessionConnectionListener);
//        try {
//            InterProcessMutex lock = new InterProcessMutex(client, LOCK_PATH + path);
//            if (lock.acquire(waitSeconds, TimeUnit.SECONDS)) {
//                return lock;
//            }
//
//        } catch (Exception e) {
//            log.error("get zookeeper lock error", e);
//        }
//        return null;
//    }
//
//    public static void releaseLock(InterProcessMutex lock) {
//        if (lock != null && lock.isAcquiredInThisProcess()) {
//            try {
//                lock.release();
//            } catch (Exception e) {
//                log.error("release lock error", e);
//            }
//        }
//    }
//
//    @PreDestroy
//    public void destory() {
//        client.close();
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        String zookeeperAddress = zookeeperProperties.getZkConnectString();
//        RetryPolicy retryPolicy = new ExponentialBackoffRetry(2000, 3);
//        client = CuratorFrameworkFactory.builder().connectString(zookeeperAddress).retryPolicy(retryPolicy)
//                .sessionTimeoutMs(6000).build();
//        client.start();
//    }
//
//    /**
//     * 监听，当zk的session失效时，重新连接session并且创建临时节点
//     */
//    static class SessionConnectionListener implements ConnectionStateListener {
//
//        private String path;
//        private int waitSeconds;
//
//        public SessionConnectionListener(String path, int waitSeconds) {
//            this.path = path;
//            this.waitSeconds = waitSeconds;
//        }
//
//        @Override
//        public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
//            if (connectionState == ConnectionState.LOST) {
//                log.error("zk session超时");
//                while (true) {
//                    try {
//                        if (curatorFramework.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
//                            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(LOCK_PATH + path);
//                            log.error("{} 重新获取锁成功", path);
//                            break;
//                        }
//                    } catch (InterruptedException e) {
//                        log.error("sessionConnectionListener error", e);
//                        break;
//                    } catch (Exception e) {
//                        log.error("sessionConnectionListener error", e);
//                    }
//                }
//            }
//        }
//    }
//}