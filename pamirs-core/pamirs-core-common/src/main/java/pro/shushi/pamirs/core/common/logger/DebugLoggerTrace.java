package pro.shushi.pamirs.core.common.logger;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.function.Supplier;

/**
 * 调试日志追踪
 *
 * @author Adamancy Zhang at 17:04 on 2025-03-26
 */
@Component
public class DebugLoggerTrace implements SceneAnalysisDebugTraceApi {

    private static final String LOGGER_DEBUG_SCENE = "调试日志";

    private static final HoldKeeper<DebugLoggerTrace> debugTraceHolder = new HoldKeeper<>();

    public static void out(String message) {
        debug(() -> message);
    }

    private static void debug(Supplier<Object> traceSupplier) {
        try {
            if (!SceneAnalysisDebugTraceApi.isDebug()) {
                return;
            }
            debugTraceHolder.supply(() -> BeanDefinitionUtils.getBean(DebugLoggerTrace.class)).addDebugTrace(traceSupplier);
        } catch (Throwable ignored) {
            // do nothing.
        }
    }

    @Override
    public String scene() {
        return LOGGER_DEBUG_SCENE;
    }
}
