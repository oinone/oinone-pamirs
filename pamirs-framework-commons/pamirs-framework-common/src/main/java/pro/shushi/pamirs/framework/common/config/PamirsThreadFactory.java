package pro.shushi.pamirs.framework.common.config;

import jakarta.annotation.Nonnull;
import jdk.internal.access.SharedSecrets;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Pamirs线程工厂
 *
 * @author Adamancy Zhang on 2021-03-30 00:12
 */
public class PamirsThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean inheritThreadLocals;

    public PamirsThreadFactory(String namePrefix) {
        this(namePrefix, true);
    }

    public PamirsThreadFactory(String namePrefix, boolean inheritThreadLocals) {
        this.namePrefix = namePrefix + "-" + POOL_NUMBER.getAndIncrement()
                + "-thread-";
        this.inheritThreadLocals = inheritThreadLocals;
    }

    @Override
    public Thread newThread(@Nonnull Runnable r) {
        Thread t;
        String threadName = namePrefix + threadNumber.getAndIncrement();
        if (inheritThreadLocals) {
            t = new Thread(null, r, threadName, 0);
        } else {
            t = SharedSecrets.getJavaLangAccess().newThreadWithAcc(r, null);
            t.setName(threadName);
        }
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

    public static Integer getAvailableProcessors() {
        return Math.max(Runtime.getRuntime().availableProcessors(), PamirsGlobalThreadConfig.GLOBAL_ASYNC_EXECUTOR_THREAD_COUNT);
    }
}
