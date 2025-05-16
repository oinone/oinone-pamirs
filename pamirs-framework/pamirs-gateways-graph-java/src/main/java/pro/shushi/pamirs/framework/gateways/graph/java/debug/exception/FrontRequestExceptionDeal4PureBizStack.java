package pro.shushi.pamirs.framework.gateways.graph.java.debug.exception;

import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.StackTraceConstants;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestExceptionDeal;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 异常时增加业务堆栈返回
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/3 2:41 下午
 */
@Component
public class FrontRequestExceptionDeal4PureBizStack implements FrontRequestExceptionDeal {

    @Override
    public void stackTrace(DataFetcherExceptionHandlerParameters handlerParameters, DataFetcherExceptionHandlerResult result, Throwable exception, ExecutionPath path) {
        Throwable e = ExceptionUtils.getRootCause(exception);
        Map<String, Object> pureMap = new LinkedHashMap<>();
        pureMap.put("异常类", e.getClass().getTypeName());
        pureMap.put("异常信息", e.getMessage());
        Map<String, Object> pureMap1 = new LinkedHashMap<>();
        pureMap.put("实际问题第一现场", pureMap1);
        StackTraceElement firstTraceElement = Optional.ofNullable(e.getStackTrace()).filter(v -> v.length >= 1).map(v -> v[0]).orElse(null);
        if (firstTraceElement == null) {
            return;
        }
        if (firstTraceElement.getClassName().equals(PamirsException.Builder.class.getName())) {
            pureMap1.put("类", e.getStackTrace()[1].getClassName());
            pureMap1.put("方法", e.getStackTrace()[1].getMethodName());
            pureMap1.put("代码行", e.getStackTrace()[1].getLineNumber());
        } else {
            pureMap1.put("类", firstTraceElement.getClassName());
            pureMap1.put("方法", firstTraceElement.getMethodName());
            pureMap1.put("代码行", firstTraceElement.getLineNumber());
        }
        result.getErrors().add(ClientGraphQLError.build(StackTraceConstants.STACKTRACE_PURE_BIZ_STACK, JsonUtils.toJSONString(pureMap)));
    }

    @Override
    public int priority() {
        return 100;
    }
}
