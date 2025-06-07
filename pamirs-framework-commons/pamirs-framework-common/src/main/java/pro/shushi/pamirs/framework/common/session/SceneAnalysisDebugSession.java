package pro.shushi.pamirs.framework.common.session;

import com.alibaba.ttl.TransmittableThreadLocal;
import graphql.ExecutionInput;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.FrontRequestInitDeal;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景追踪上下文Session
 *
 * @author Adamancy Zhang at 16:27 on 2024-05-20
 */
@Component
public class SceneAnalysisDebugSession implements FrontRequestInitDeal, SessionClearApi {

    private static final TransmittableThreadLocal<Map<String, Map<String, List<Object>>>> DEBUG_THREAD_LOCAL = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<Boolean> INNER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    private static List<Object> init(String scene) {
        return getSceneDebugInfoMap(scene).computeIfAbsent(getCurrentThreadId(), k -> new LinkedList<>());
    }

    public static Map<String, List<Object>> getSceneDebugInfoMap(String scene) {
        Map<String, Map<String, List<Object>>> storage = DEBUG_THREAD_LOCAL.get();
        if (storage == null) {
            return new HashMap<>(4);
        }
        return storage.computeIfAbsent(scene, k -> new LinkedHashMap<>(4));
    }

    public static void addCurrentThreadSceneDebugInfo(String scene, Object info) {
        init(scene).add(info);
    }

    public static void addCurrentThreadSceneDebugInfo(String scene, int index, Object info) {
        init(scene).add(index, info);
    }

    public static List<Object> getCurrentThreadSceneDebugInfoList(String scene) {
        return init(scene);
    }

    @Override
    public void init(ExecutionInput executionInput) {
        if (DEBUG_THREAD_LOCAL.get() == null) {
            DEBUG_THREAD_LOCAL.set(new ConcurrentHashMap<>(8));
        }
    }

    @Override
    public void init(Invoker<?> invoker, Invocation invocation) {
        if (DEBUG_THREAD_LOCAL.get() == null) {
            DEBUG_THREAD_LOCAL.set(new ConcurrentHashMap<>(8));
        }
    }

    @Override
    public void clear() {
        DEBUG_THREAD_LOCAL.remove();
        INNER_THREAD_LOCAL.remove();
    }

    private static String getCurrentThreadId() {
        return String.valueOf(Thread.currentThread().getId());
    }

    public static class AnalysisDebugHintApi implements AutoCloseable {

        public static Boolean isInner() {
            return INNER_THREAD_LOCAL.get() != null && INNER_THREAD_LOCAL.get();
        }

        public static AnalysisDebugHintApi use(Boolean inner) {
            return new AnalysisDebugHintApi(inner);
        }

        public AnalysisDebugHintApi(Boolean inner) {
            INNER_THREAD_LOCAL.set(inner);
        }

        @Override
        public void close() {
            if (INNER_THREAD_LOCAL.get() != null) {
                INNER_THREAD_LOCAL.remove();
            }
        }
    }
}
