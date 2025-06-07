package pro.shushi.pamirs.core.common.logger.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;

/**
 * 调试追踪日志过滤器
 *
 * @author Adamancy Zhang at 20:12 on 2025-03-26
 */
public class DebugLoggerTraceTurboFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (SceneAnalysisDebugTraceApi.isDebug()) {
            int debugLevel = SceneAnalysisDebugTraceApi.debugLevel();
            if (debugLevel == 3 && level.isGreaterOrEqual(Level.DEBUG)) {
                return FilterReply.ACCEPT;
            }
            if (debugLevel == 4 && level.isGreaterOrEqual(Level.TRACE)) {
                return FilterReply.ACCEPT;
            }
        }
        return null;
    }
}
