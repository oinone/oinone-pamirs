package pro.shushi.pamirs.framework.common.api;


import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.common.session.SceneAnalysisDebugSession;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 根据场景进行问题分析API
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/5 2:02 下午
 */
public interface SceneAnalysisDebugTraceApi {

    String STACKTRACE_FLAG_1 = "stacktrace";

    String STACKTRACE_FLAG_2 = "debug";

    default void addDebugTrace(Supplier<Object> debugFunction) {
        Object debugInfo = null;
        try (SceneAnalysisDebugSession.AnalysisDebugHintApi debugHintApi = SceneAnalysisDebugSession.AnalysisDebugHintApi.use(Boolean.TRUE)) {
            debugInfo = debugFunction.get();
        } catch (Throwable e) {
            //忽略
        }
        if (debugInfo == null) {
            return;
        }
        if (StringUtils.isBlank(scene())) {
            return;
        }
        SceneAnalysisDebugSession.addCurrentThreadSceneDebugInfo(scene(), debugInfo);
    }

    default boolean isAdded() {
        String scene = scene();
        if (StringUtils.isBlank(scene)) {
            return Boolean.TRUE;
        }
        return MapUtils.isNotEmpty(SceneAnalysisDebugSession.getSceneDebugInfoMap(scene));
    }

    String scene();

    static boolean isDebug() {
        boolean result = !SceneAnalysisDebugSession.AnalysisDebugHintApi.isInner() && PamirsSession.getRequestVariables() != null &&
                PamirsSession.getRequestVariables().getParameterMap() != null &&
                (StringUtils.isNotBlank(PamirsSession.getRequestVariables().getParameter(STACKTRACE_FLAG_1))
                        || StringUtils.isNotBlank(PamirsSession.getRequestVariables().getParameter(STACKTRACE_FLAG_2))
                );
        if (result) {
            //业务扩展优先判断
            if (!Spider.getDefaultExtension(IsDebugExtApi.class).isDebug()) {
                return Boolean.FALSE;
            }
        }
        return result;
    }

    /**
     * <h3>日志级别</h3>
     * <ul>
     *     <li>默认调试: 1</li>
     *     <li>权限调试: 2</li>
     *     <li>Debug级别日志调试: 3</li>
     *     <li>Trace级别日志调试: 4</li>
     * </ul>
     *
     * @return 调试级别
     */
    static int debugLevel() {
        String debugLevelString = PamirsSession.getRequestVariables().getParameter(STACKTRACE_FLAG_2);
        if (StringUtils.isBlank(debugLevelString)) {
            return 1;
        }
        try {
            return Integer.parseInt(debugLevelString);
        } catch (Throwable ignored) {
        }
        return 1;
    }

    default Map<String, Object> stackTrace() {
        String scene = scene();
        if (StringUtils.isBlank(scene)) {
            return null;
        }
        Map<String, List<Object>> debugInfoMap = SceneAnalysisDebugSession.getSceneDebugInfoMap(scene);
        if (MapUtils.isEmpty(debugInfoMap)) {
            return null;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(scene, debugInfoMap);
        return result;
    }
}
