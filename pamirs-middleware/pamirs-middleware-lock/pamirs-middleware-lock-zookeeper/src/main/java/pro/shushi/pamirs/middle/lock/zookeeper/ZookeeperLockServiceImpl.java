package pro.shushi.pamirs.middle.lock.zookeeper;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.middle.lock.LockConsumer;
import pro.shushi.pamirs.middle.lock.LockResult;
import pro.shushi.pamirs.middle.lock.LockService;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;
import pro.shushi.pamirs.middleware.zookeeper.util.ZookeeperHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Adamancy Zhang
 * @date 2020-12-24 10:52
 */
@Service(ZookeeperLockServiceImpl.BEAN_NAME)
public class ZookeeperLockServiceImpl implements LockService, InitializingBean {

    public static final String BEAN_NAME = "zookeeperLockService";

    private static final Logger log = LoggerFactory.getLogger(ZookeeperLockServiceImpl.class);

    @Autowired
    private ZookeeperService zookeeperService;

    private final Map<String, InterProcessMutex> interProcessMutexMap = new ConcurrentHashMap<>();

    @Override
    public String getFullPath(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        path = ZookeeperHelper.repairPath(path);
        return LOCK_ROOT_PATH + path;
    }

    @Override
    public LockResult lock(String key, LockConsumer lockConsumer) {
        return lock(key, 1, TimeUnit.SECONDS, lockConsumer);
    }

    @Override
    public LockResult lock(String key, int waitTime, TimeUnit waitTimeUnit, LockConsumer lockConsumer) {
        key = getFullPath(key);

        //自动释放锁
        boolean isAutoReleaseLocked = true;

        InterProcessMutex lock = null;
        try {
            //注册连接状态监听
            zookeeperService.registerConnectionStateListener(key, new SessionConnectionListener(zookeeperService, key));

            //创建锁
            lock = new InterProcessMutex(zookeeperService.getClient(), zookeeperService.getFullPath(key));

            //申请锁
            if (lock.acquire(waitTime, waitTimeUnit)) {
                //获取锁成功
                try {
                    isAutoReleaseLocked = lockConsumer.accept();
                } catch (Throwable e) {
                    //消费失败
                    log.error("consumer zookeeper lock error [routePath {}]", key, e);
                    return LockResult.failure(key, LockResult.Type.CONSUMER, e);
                }
            } else {
                //获取锁失败
                lock = null;
                isAutoReleaseLocked = false;
                log.error("get zookeeper lock error [routePath {}]", key);
                return LockResult.failure(key, LockResult.Type.GET, null);
            }
        } catch (Throwable e) {
            //申请锁失败
            log.error("apply zookeeper lock error [routePath {}]", key, e);
            return LockResult.failure(key, LockResult.Type.APPLY, e);
        } finally {
            if (isAutoReleaseLocked) {
                releaseLocked0(key, lock);
            } else {
                if (lock != null) {
                    interProcessMutexMap.put(key, lock);
                }
            }
        }
        return LockResult.success(key);
    }

    @Override
    public LockResult releaseLocked(String key) {
        key = getFullPath(key);
        LockResult releaseResult = releaseLocked0(key, interProcessMutexMap.get(key));
        if (releaseResult.isSuccess()) {
            zookeeperService.cancellationConnectionStateListener(key);
            interProcessMutexMap.remove(key);
            return releaseResult;
        }
        return releaseResult;
    }

    private LockResult releaseLocked0(String key, InterProcessMutex lock) {
        if (lock != null && lock.isAcquiredInThisProcess()) {
            try {
                lock.release();
            } catch (Exception e) {
                log.error("release zookeeper lock error [routePath {}]", key, e);
                return LockResult.failure(key, LockResult.Type.RELEASE, e);
            }
        }
        return LockResult.success(key);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        zookeeperService.create(LockService.LOCK_ROOT_PATH);
    }
}
