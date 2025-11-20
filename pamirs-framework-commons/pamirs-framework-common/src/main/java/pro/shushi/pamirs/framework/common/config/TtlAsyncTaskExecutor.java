package pro.shushi.pamirs.framework.common.config;

import com.alibaba.ttl.threadpool.TtlExecutors;

import javax.annotation.Nonnull;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 传递线程变量的 请求线程执行器
 * <p>
 * 2023/12/02
 *
 * @author wangxian@shushi.pro
 * @version 1.0.0
 */
public class TtlAsyncTaskExecutor {

    private static final ExecutorService executorService;

    static {
        int nThreads = nThreads();
        executorService = TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new TtlThreadFactory()));
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static int nThreads() {
        return Math.max(PamirsThreadFactory.getAvailableProcessors() * 2, PamirsGlobalThreadConfig.GLOBAL_TTL_ASYNC_EXECUTOR_THREAD_COUNT);
    }

    private static class TtlThreadFactory implements ThreadFactory {

        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

        @Override
        public Thread newThread(@Nonnull Runnable r) {
            Thread t = new Thread(r);
            t.setName("p.s.p.global.TtlAsyncTaskExecutor-" + POOL_NUMBER.getAndIncrement());
            t.setUncaughtExceptionHandler(PamirsThreadFactory.COMMON_UNCAUGHT_EXCEPTION_HANDLER_INSTANCE);
            return t;
        }
    }
}
