package pro.shushi.pamirs.channel.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ChannelThreadFactory
 *
 * @author yakir on 2019/05/09 14:22.
 */
public class ChannelThreadFactory implements ThreadFactory {

    private static final Logger log = LoggerFactory.getLogger(ChannelThreadFactory.class);

    private final String        name;
    private final boolean       daemon;
    private final ThreadGroup   group;
    private final AtomicInteger threadNumber = new AtomicInteger(0);

    private final static String                   DEFAULT_NAME         = "channel-worker";
    private final static UncaughtExceptionHandler UNCAUGHT_EXP_HANDLER = (Thread t, Throwable e) -> {
        if (e instanceof InterruptedException || (e.getCause() != null && e.getCause() instanceof InterruptedException)) {
            log.error("EmtWork Thread error, msg: [{}]", e.getMessage(), e);
            return;
        }
        log.error("from ".concat(t.getName()), e);
    };

    public ChannelThreadFactory() {
        this(DEFAULT_NAME, false);
    }

    public ChannelThreadFactory(String name) {
        this(name, false);
    }

    public ChannelThreadFactory(String name, boolean daemon) {
        this.name   = name;
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        group = null != s ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, name + "-" + threadNumber.getAndIncrement(), 0L);
        t.setDaemon(daemon);
        t.setUncaughtExceptionHandler(UNCAUGHT_EXP_HANDLER);
        return t;
    }

}