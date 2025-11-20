package pro.shushi.pamirs.framework.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.api.ScheduleApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 全局计划调度线程池
 *
 * @author Adamancy Zhang on 2021-03-30 14:28
 */
@Slf4j
@Configuration
public class ScheduledTaskExecutorConfiguration {

    public static final String SCHEDULED_THREAD_POOL_EXECUTOR = "globalScheduledThreadPoolExecutor";

    @Bean(SCHEDULED_THREAD_POOL_EXECUTOR)
    public ScheduledExecutorService globalScheduledThreadPoolExecutor() {
        int corePoolSize = PamirsThreadFactory.getAvailableProcessors();
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(corePoolSize, new PamirsThreadFactory("scheduled"));
        log.info("Register global scheduled executor service [CoreSize {}]", corePoolSize);
        Runnable task = () -> {
            List<ScheduleApi> scheduleApis = BeanDefinitionUtils.getBeansOfTypeByOrdered(ScheduleApi.class);
            for (ScheduleApi scheduleApi : scheduleApis) {
                try {
                    scheduleApi.run();
                } catch (Throwable ignored) {
                }
            }
        };
        executor.scheduleAtFixedRate(task, 5, 30, TimeUnit.MINUTES);
        return executor;

    }
}
