package pro.shushi.pamirs.framework.common.config;

import javax.annotation.Nonnull;
import java.security.AccessController;
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
    private final ThreadGroup group;
    private final boolean inheritThreadLocals;

    public PamirsThreadFactory(String namePrefix) {
        this(namePrefix, true);
    }

    public PamirsThreadFactory(String namePrefix, boolean inheritThreadLocals) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix + "-" + POOL_NUMBER.getAndIncrement()
                + "-thread-";
        this.inheritThreadLocals = inheritThreadLocals;
    }

    @Override
    public Thread newThread(@Nonnull Runnable r) {
        Thread t;
        String threadName = namePrefix + threadNumber.getAndIncrement();
        if (inheritThreadLocals) {
            t = new Thread(group, r, threadName, 0);
        } else {
            t = sun.misc.SharedSecrets.getJavaLangAccess().newThreadWithAcc(r, AccessController.getContext());
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
