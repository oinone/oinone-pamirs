package pro.shushi.pamirs.framework.gateways.graph.java.pool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RequestThreadPool
 *
 * @author yakir on 2025/03/19 09:55.
 */
@Slf4j
@Configuration
public class RequestThreadPool {

    private static final AtomicInteger deferredThreadIncr = new AtomicInteger();

    private final ExecutorService executorService;

    public RequestThreadPool(@Autowired RequestThreadPoolConfig requestThreadPoolConfig) {
        if (requestThreadPoolConfig.isDeferred()) {
            executorService = new ThreadPoolExecutor(requestThreadPoolConfig.getCoreSize(), requestThreadPoolConfig.getMaxSize(),
                    requestThreadPoolConfig.getKeepAliveTime(),
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(),
                    new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            return new Thread(r, "Deferred" + deferredThreadIncr.getAndIncrement());
                        }
                    });
        } else {
            executorService = null;
        }
    }

    public void submit(Runnable run, DeferredResult<String> deferredResult) {
        if (null == executorService) {
            run.run();
        } else {
            executorService.submit(run);
        }
    }
}
