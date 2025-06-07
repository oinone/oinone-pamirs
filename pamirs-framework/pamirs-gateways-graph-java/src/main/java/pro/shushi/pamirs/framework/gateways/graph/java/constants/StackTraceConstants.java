package pro.shushi.pamirs.framework.gateways.graph.java.constants;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * StackTrace 常量
 * <p>
 * 2024/04/03  下午
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 */
public interface StackTraceConstants {

    Pair<String, String> STACKTRACE_EXCEPTION = ImmutablePair.of("exception", "全异常堆栈");
    Pair<String, String> STACKTRACE_ROOT_EXCEPTION = ImmutablePair.of("rootException", "根异常堆栈");
    Pair<String, String> STACKTRACE_MODEL = ImmutablePair.of("model", "模型信息");
    Pair<String, String> STACKTRACE_CONFIGURE = ImmutablePair.of("configure", "环境配置信息");
    Pair<String, String> STACKTRACE_FUNCTION = ImmutablePair.of("function", "函数信息");
    Pair<String, String> STACKTRACE_BIZ_STACK = ImmutablePair.of("bizStack", "业务与Oinone相关堆栈");
    Pair<String, String> STACKTRACE_HALF_PURE_BIZ_STACK = ImmutablePair.of("halfPureBizStack", "仅业务相关堆栈");
    Pair<String, String> STACKTRACE_PURE_BIZ_STACK = ImmutablePair.of("pureBizStack", "异常发生地");

    Pair<String, String> STACKTRACE_SESSION = ImmutablePair.of("stacktraceSession", "PamirsSession上下文信息");
    Pair<String, String> STACKTRACE_GQL_REQUEST_CONTEXT = ImmutablePair.of("stacktraceGqlRequestContext", "GraphQL请求上下文摘要信息");
    Pair<String, String> STACKTRACE_SCENE_ANALYSIS = ImmutablePair.of("stacktraceSceneAnalysis", "场景追踪");

}
