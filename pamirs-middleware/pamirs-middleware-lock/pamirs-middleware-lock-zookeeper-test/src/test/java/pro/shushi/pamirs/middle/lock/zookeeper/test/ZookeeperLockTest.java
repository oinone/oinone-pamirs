package pro.shushi.pamirs.middle.lock.zookeeper.test;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {ZookeeperLockApplicationTest.class})
public class ZookeeperLockTest extends AbstractZookeeperLockTest {

    @Test
    public void tryLock001() throws InterruptedException {
        log.info("原始锁");
        executor.execute(new ZookeeperLockTestRunnable(lockService, Boolean.TRUE, LOCK1, LOCK_PATH) {
            @Override
            public String getTarget() {
                return target;
            }

            @Override
            public void setTarget() {
                target = "1";
            }
        });

        log.info("修改锁");
        executor.execute(new ZookeeperLockTestRunnable(lockService, Boolean.TRUE, LOCK2, LOCK_PATH) {
            @Override
            public String getTarget() {
                return target;
            }

            @Override
            public void setTarget() {
                target = "2";
            }
        });

        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void tryLock002() throws InterruptedException {
        log.info("非自动释放原始锁");
        executor.execute(new ZookeeperLockTestRunnable(lockService, Boolean.FALSE, LOCK1, LOCK_PATH) {
            @Override
            public String getTarget() {
                return target;
            }

            @Override
            public void setTarget() {
                target = "1";
            }
        });

        log.info("非自动释放修改锁");
        executor.execute(new ZookeeperLockTestRunnable(lockService, Boolean.FALSE, LOCK2, LOCK_PATH) {
            @Override
            public String getTarget() {
                return target;
            }

            @Override
            public void setTarget() {
                target = "2";
            }
        });

        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void tryLock003() throws InterruptedException {
        log.info("同线程锁测试");
        new ZookeeperLockTestRunnable(lockService, Boolean.FALSE, LOCK1, LOCK_PATH) {
            @Override
            public String getTarget() {
                return target;
            }

            @Override
            public void setTarget() {
                target = "1";
            }
        }.run();

        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void tryLock004() throws InterruptedException {
        log.info("子线程加锁，主线程解锁测试");
        executor.execute(new ZookeeperLockTestRunnable(lockService, Boolean.FALSE, LOCK1, LOCK_PATH) {
            @Override
            public String getTarget() {
                return target;
            }

            @Override
            public void setTarget() {
                target = "1";
            }
        });

        TimeUnit.SECONDS.sleep(5);
    }

    @After
    public void releaseLock() {
        log.info("释放锁结果 {}", lockService.releaseLocked(LOCK_PATH));
        log.info("Release All Lock.");
    }
}
