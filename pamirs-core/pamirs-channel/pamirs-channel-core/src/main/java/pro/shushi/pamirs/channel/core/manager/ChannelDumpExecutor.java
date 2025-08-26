package pro.shushi.pamirs.channel.core.manager;

import com.alibaba.ttl.threadpool.TtlExecutors;
import pro.shushi.pamirs.channel.core.utils.ChannelThreadFactory;
import pro.shushi.pamirs.framework.common.config.PamirsGlobalThreadConfig;
import pro.shushi.pamirs.framework.common.config.PamirsThreadFactory;

import java.util.concurrent.*;

/**
 * ChannelDumpExecutor
 *
 * @author yakir on 2025/08/26 10:27.
 */
public class ChannelDumpExecutor implements AutoCloseable {

    private static volatile ExecutorService executorService;

    public ChannelDumpExecutor(int size) {

        if (null == executorService) {
            if (size <= 0) {
                size = Math.max(PamirsThreadFactory.getAvailableProcessors() * 2, PamirsGlobalThreadConfig.GLOBAL_TTL_ASYNC_EXECUTOR_THREAD_COUNT);
            }
            executorService = TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(size, size,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(),
                    new ChannelThreadFactory()));

        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public void close() throws Exception {
        if (null != executorService
                && !executorService.isShutdown()
                && !executorService.isTerminated()) {
            executorService.shutdown();
            executorService = null;
        }
    }
}
