package pro.shushi.pamirs.framework.gateways.graph.java.debug.exception;

import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.StackTraceConstants;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestExceptionDeal;
import pro.shushi.pamirs.framework.gateways.graph.java.utils.SessionExtendUtils;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 异常时增加业务堆栈返回
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/3 2:41 下午
 */
@Component
public class FrontRequestExceptionDeal4HalfPureBizStack implements FrontRequestExceptionDeal {

    @Override
    public void stackTrace(DataFetcherExceptionHandlerParameters handlerParameters, DataFetcherExceptionHandlerResult result, Throwable exception, ExecutionPath path) {
        String modelName = SessionExtendUtils.gqlSegmentNameToModelNameUtils(path.getParent().getSegmentName());
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfigByName(modelName);
        ModuleDefinition moduleDefinition = PamirsSession.getContext().getModule(modelConfig.getModule());
        String bizPackFlag = null;
        String[] packages = modelConfig.getLname().split("\\.");
        if (packages != null && packages.length > 2) {
            bizPackFlag = packages[1];
        }
        Throwable e = ExceptionUtils.getRootCause(exception);
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        List slowLogs = new ArrayList();

        for (StackTraceElement element : stackTraceElements) {
            if (moduleDefinition.getPackagePrefix() != null) {
                for (String bizPackage : moduleDefinition.getPackagePrefix()) {
                    if (element.getClassName().indexOf(bizPackage) > 0) {
                        StringBuffer slowLog = new StringBuffer();
                        slowLog.append(element.getClassName()).append(":").append(element.getMethodName()).append(":").append(element.getLineNumber());
                        slowLogs.add(slowLog.toString());
                    }
                }
            } else {
                if (StringUtils.isNotBlank(bizPackFlag)) {
                    if (element.getClassName().indexOf(bizPackFlag) > 0) {
                        StringBuffer slowLog = new StringBuffer();
                        slowLog.append(element.getClassName()).append(":").append(element.getMethodName()).append(":").append(element.getLineNumber());
                        slowLogs.add(slowLog.toString());
                    }
                }
            }
        }
        result.getErrors().add(ClientGraphQLError.build(StackTraceConstants.STACKTRACE_HALF_PURE_BIZ_STACK, JsonUtils.toJSONString(slowLogs)));
    }

    @Override
    public int priority() {
        return 101;
    }
}
