package pro.shushi.pamirs.sso.api.executor;

import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class SsoScheduledExecutor {

    private final static ScheduledExecutorService scheduledExecutorService = TtlExecutors.getTtlScheduledExecutorService(Executors.newScheduledThreadPool(nThreads(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("p.s.p.sso.oauth2");
            return t;
        }
    }));

    public static ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public static Integer nThreads() {
        return 16;
    }

}