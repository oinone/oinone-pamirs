package pro.shushi.pamirs.middle.lock;

import java.util.concurrent.TimeUnit;

/**
 * 锁服务
 */
public interface LockService {

    String LOCK_ROOT_PATH = "/lock";

    String getFullPath(String path);

    LockResult lock(String key, LockConsumer lockConsumer);

    LockResult lock(String key, int waitTime, TimeUnit waitTimeUnit, LockConsumer lockConsumer);

    LockResult releaseLocked(String key);
}
