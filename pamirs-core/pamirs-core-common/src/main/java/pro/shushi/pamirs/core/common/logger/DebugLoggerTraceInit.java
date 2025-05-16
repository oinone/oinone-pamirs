package pro.shushi.pamirs.core.common.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.logger.logback.DebugLoggerTraceLogbackAppender;
import pro.shushi.pamirs.framework.common.api.IsDebugExtApi;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.concurrent.Executor;

import static pro.shushi.pamirs.framework.common.config.AsyncTaskExecutorConfiguration.FIXED_THREAD_POOL_EXECUTOR;

/**
 * 调试日志追踪初始化
 *
 * @author Adamancy Zhang at 19:54 on 2025-03-26
 */
@Order
@Component
public class DebugLoggerTraceInit implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired(required = false)
    @Qualifier(FIXED_THREAD_POOL_EXECUTOR)
    private Executor globalFixedThreadPoolExecutor;

    private static void init() {
        Logger logger = LoggerFactory.getLogger(DebugLoggerTraceInit.class);
        DebugLoggerTraceLogbackAppender.init(logger);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (Spider.getDefaultExtension(IsDebugExtApi.class).isDebug()) {
            globalFixedThreadPoolExecutor.execute(DebugLoggerTraceInit::init);
        }
    }
}
