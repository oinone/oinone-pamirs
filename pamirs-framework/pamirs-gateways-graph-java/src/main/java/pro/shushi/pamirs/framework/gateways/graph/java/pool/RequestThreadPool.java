package pro.shushi.pamirs.framework.gateways.graph.java.pool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import pro.shushi.pamirs.framework.common.config.PamirsThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RequestThreadPool
 *
 * @author yakir on 2025/03/19 09:55.
 */
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
                    r -> {
                        Thread t = new Thread(r, "Deferred" + deferredThreadIncr.getAndIncrement());
                        t.setUncaughtExceptionHandler(PamirsThreadFactory.COMMON_UNCAUGHT_EXCEPTION_HANDLER_INSTANCE);
                        return t;
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
