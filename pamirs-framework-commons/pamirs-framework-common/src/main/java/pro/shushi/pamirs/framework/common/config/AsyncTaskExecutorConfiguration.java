package pro.shushi.pamirs.framework.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.concurrent.Executor;

/**
 * 全局异步任务线程池
 * <p>
 * 使用该线程池作为Spring异步任务执行线程池
 * </p>
 *
 * @author Adamancy Zhang at 18:46 on 2024-07-09
 */
@Slf4j
@Configuration
public class AsyncTaskExecutorConfiguration extends AsyncConfigurerSupport {

    public static final String FIXED_THREAD_POOL_EXECUTOR = "globalFixedThreadPoolExecutor";

    @Bean(FIXED_THREAD_POOL_EXECUTOR)
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadFactory(new PamirsThreadFactory("fixed", false));
        int nThreads = PamirsThreadFactory.getAvailableProcessors();
        executor.setCorePoolSize(nThreads);
        executor.setMaxPoolSize(nThreads * 2);
        log.info("Register global async executor service [CoreSize {}] [MaxPoolSize {}]", executor.getCorePoolSize(), executor.getMaxPoolSize());
        return executor;
    }
}
