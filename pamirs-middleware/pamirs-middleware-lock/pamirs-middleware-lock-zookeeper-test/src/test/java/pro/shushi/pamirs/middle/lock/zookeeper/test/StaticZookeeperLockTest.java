//package pro.shushi.pamirs.middle.lock.zookeeper.test;
//
//import org.apache.curator.framework.recipes.locks.InterProcessMutex;
//import org.junit.After;
//import org.junit.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import pro.shushi.pamirs.middle.lock.zookeeper.CuratorUtils2;
//
//import java.util.concurrent.TimeUnit;
//
//@SpringBootTest(classes = {ZookeeperLockApplicationTest.class})
//public class StaticZookeeperLockTest extends AbstractZookeeperLockTest {
//
//    @Test
//    public void tryLock001() throws Exception {
//        zookeeperService.create("/lock/target");
//
//        log.info("同线程锁测试（原工具类）");
//        log.info("{}获取锁", LOCK1);
//        InterProcessMutex lock = CuratorUtils2.tryLock(LOCK_PATH, 3);
//        if (lock != null) {
//            interProcessMutexMap.put(LOCK1, lock);
//            log.info("{} 查看 [target {}]", LOCK1, target);
//            target = "1";
//            log.info("{} 更新 [target {}]", LOCK1, target);
//        } else {
//            log.error("{}获取锁失败", LOCK1);
//        }
//
//        TimeUnit.SECONDS.sleep(5);
//    }
//
//    @Test
//    public void tryLock002() throws Exception {
//        zookeeperService.create("/lock/target");
//
//        log.info("子线程加锁，主线程解锁测试（原工具类）");
//        executor.execute(() -> {
//            log.info("{}获取锁", LOCK1);
//            interProcessMutexMap.put(LOCK1, CuratorUtils2.tryLock(LOCK_PATH, 3));
//            log.info("{} 查看 [target {}]", LOCK1, target);
//            target = "1";
//            log.info("{} 更新 [target {}]", LOCK1, target);
//        });
//
//        TimeUnit.SECONDS.sleep(5);
//    }
//
//    @After
//    public void releaseLock() {
//        CuratorUtils2.releaseLock(interProcessMutexMap.get(LOCK1));
//        log.info("Release All Lock.");
//    }
//}
