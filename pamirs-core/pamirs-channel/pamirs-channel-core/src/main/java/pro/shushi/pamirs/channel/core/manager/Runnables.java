package pro.shushi.pamirs.channel.core.manager;

import pro.shushi.pamirs.channel.core.utils.ChannelThreadFactory;
import pro.shushi.pamirs.channel.core.utils.NewThreadRunsPolicy;

import java.util.concurrent.*;

/**
 * Runnables
 *
 * @author yakir on 2020/04/23 21:04.
 */
public class Runnables {

    private static final int PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            PROCESSOR_COUNT,
            PROCESSOR_COUNT,
            1L,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(),
            new ChannelThreadFactory(),
            new NewThreadRunsPolicy()
    );

    public static <T> Future<T> submit(Callable<T> callable) {

        return EXECUTOR_SERVICE.submit(callable);

    }

    public static void submit(Runnable runnable) {

        EXECUTOR_SERVICE.submit(runnable);

    }

}
