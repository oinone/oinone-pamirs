package pro.shushi.pamirs.middle.lock.zookeeper.test;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringRunner;
import pro.shushi.pamirs.middle.lock.LockService;
import pro.shushi.pamirs.middle.lock.zookeeper.ZookeeperLockServiceImpl;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractZookeeperLockTest {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier(ZookeeperLockServiceImpl.BEAN_NAME)
    protected LockService lockService;

    @Autowired
    protected ZookeeperService zookeeperService;

    protected static final Executor executor = Executors.newFixedThreadPool(3);

    protected static String target;

    protected static final String LOCK1 = "lock1";

    protected static final String LOCK2 = "lock2";

    protected static final String LOCK_PATH = "target";

    protected final Map<String, InterProcessMutex> interProcessMutexMap = new ConcurrentHashMap<>();

    @Before
    public void beforeRunner() throws Exception {
        //启动zookeeper连接
        zookeeperService.start();
        //创建测试类的根路径
        zookeeperService.create(LockService.LOCK_ROOT_PATH);
    }

    @After
    public void afterAfterRunner() throws Exception {
        //删除测试类的根路径
        zookeeperService.delete(LockService.LOCK_ROOT_PATH, Boolean.TRUE);
        //关闭zookeeper连接
        zookeeperService.close();
    }

    protected abstract class ZookeeperLockTestRunnable implements Runnable {

        private final LockService lockService;

        private final Boolean isAutoReleaseLock;

        private final String lock;

        private final String lockPath;

        public abstract String getTarget();

        public abstract void setTarget();

        public ZookeeperLockTestRunnable(LockService lockService, Boolean isAutoReleaseLock, String lock, String lockPath) {
            Assert.assertNotNull(lockService);
            Assert.assertNotNull(isAutoReleaseLock);
            Assert.assertNotNull(lock);
            Assert.assertNotNull(lockPath);

            this.lockService = lockService;
            this.isAutoReleaseLock = isAutoReleaseLock;
            this.lock = lock;
            this.lockPath = lockPath;
        }

        @Override
        public void run() {
            log.info("{} test result {}", lock, lockService.lock(lockPath, 5, TimeUnit.SECONDS, () -> {
                log.info("{} acquire lock", lock);
                try {
                    log.info("{} view [target {}]", lock, getTarget());
                    setTarget();
                    log.info("{} update [target {}]", lock, getTarget());
                    TimeUnit.SECONDS.sleep(2);
                    if (isAutoReleaseLock) {
                        log.info("{} release lock", lock);
                    }
                } catch (InterruptedException e) {
                    return isAutoReleaseLock;
                }
                return isAutoReleaseLock;
            }));
        }
    }
}
