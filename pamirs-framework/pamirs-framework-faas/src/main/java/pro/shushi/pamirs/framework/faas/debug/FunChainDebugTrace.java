package pro.shushi.pamirs.framework.faas.debug;

import com.alibaba.ttl.TransmittableThreadLocal;
import graphql.ExecutionInput;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.FrontRequestInitDeal;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;
import pro.shushi.pamirs.framework.common.session.SceneAnalysisDebugSession;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.api.core.faas.computer.FunctionComputer;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * debug调试收集函数调用链
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/5 1:51 下午
 */

@Component
public class FunChainDebugTrace implements SceneAnalysisDebugTraceApi, FrontRequestInitDeal {

    private static final String FUN_CHAIN_DEBUG_SCENE = "pamirs-framework-faas.FunChainDebugTrace.trace_title";
    private static final TransmittableThreadLocal<Map<String, Deque<StringBuffer>>> DEBUG_DEQUE_LOCAL = new TransmittableThreadLocal<>();

    public static void debug(Function function, FunctionComputer computer, long startTime, int anchorIndex) {
        try {
            if (!SceneAnalysisDebugTraceApi.isDebug()) {
                return;
            }
            long time = System.currentTimeMillis() - startTime;
            BeanDefinitionUtils.getBean(FunChainDebugTrace.class).addDebugTrace(anchorIndex, () -> {
                StringBuilder sb = new StringBuilder();
                sb.append(getS());
                sb.append(I18nUtils.getMessage("pamirs-framework-faas.FunChainDebugTrace.time_elapsed")).append(time).append("]");
                sb.append("namespace :[").append(function.getNamespace()).append("]");
                sb.append(", fun :[").append(function.getFun()).append("]");
                if (StringUtils.isNotBlank(function.getBeanName())) {
                    sb.append(", beanName :[").append(function.getBeanName()).append("]");
                }
                if (StringUtils.isNotBlank(function.getClazz())) {
                    sb.append(", clazz :[").append(function.getClazz()).append("]");
                }
                sb.append(", ").append(I18nUtils.getMessage("pamirs-framework-faas.FunChainDebugTrace.fun_computer")).append(computer.type().name()).append("]");
                sb.append(System.lineSeparator());
                return sb.toString();
            });
        } catch (Throwable ignored) {
        }
    }

    protected void addDebugTrace(int index, Supplier<Object> debugFunction) {
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
        SceneAnalysisDebugSession.addCurrentThreadSceneDebugInfo(scene(), index, debugInfo);
    }

    @Override
    public Map<String, Object> stackTrace() {
        Map<String, List<Object>> debugInfoMap = SceneAnalysisDebugSession.getSceneDebugInfoMap(scene());
        if (MapUtils.isEmpty(debugInfoMap)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Map.Entry<String, List<Object>> entry : debugInfoMap.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(System.lineSeparator());
            }
            sb.append(I18nUtils.getMessage("pamirs-framework-faas.FunChainDebugTrace.thread_id")).append(entry.getKey()).append(System.lineSeparator());
            for (Object s : entry.getValue()) {
                sb.append(s.toString());
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(scene(), sb.toString());
        return result;
    }

    public static int anchor() {
        return SceneAnalysisDebugSession.getCurrentThreadSceneDebugInfoList(BeanDefinitionUtils.getBean(FunChainDebugTrace.class).scene()).size();
    }

    public static void push() {
        Optional.ofNullable(DEBUG_DEQUE_LOCAL.get()).ifPresent(v -> v.computeIfAbsent(getCurrentThreadId(), k -> new ArrayDeque<>())
                .push(new StringBuffer("|--")));
    }

    public static void pop() {
        Optional.ofNullable(DEBUG_DEQUE_LOCAL.get()).ifPresent(v -> v.get(getCurrentThreadId()).pop());
    }

    private static String getCurrentThreadId() {
        return String.valueOf(Thread.currentThread().getId());
    }

    public static String getS() {
        StringBuilder sb = new StringBuilder();
        Deque<StringBuffer> list = Optional.ofNullable(DEBUG_DEQUE_LOCAL.get()).map(v -> v.get(getCurrentThreadId())).orElse(null);
        if (list != null) {
            for (StringBuffer s : list) {
                sb.append(s.toString());
            }
        }
        return sb.toString();
    }

    @Override
    public void init(ExecutionInput executionInput) {
        if (DEBUG_DEQUE_LOCAL.get() == null) {
            DEBUG_DEQUE_LOCAL.set(new LinkedHashMap<>());
        }
    }

    @Override
    public void init(Invoker<?> invoker, Invocation invocation) {
        if (DEBUG_DEQUE_LOCAL.get() == null) {
            DEBUG_DEQUE_LOCAL.set(new LinkedHashMap<>());
        }
    }

    @Override
    public void clear() {
        DEBUG_DEQUE_LOCAL.remove();
    }

    @Override
    public String scene() {
        return I18nUtils.getMessage(FUN_CHAIN_DEBUG_SCENE);
    }
}
