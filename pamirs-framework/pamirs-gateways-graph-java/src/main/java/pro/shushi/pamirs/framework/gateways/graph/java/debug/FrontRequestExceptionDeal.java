package pro.shushi.pamirs.framework.gateways.graph.java.debug;

import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;


/**
 * 前端请求异常处理接口
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/3 2:41 下午
 */
public interface FrontRequestExceptionDeal extends Prioritized, CommonApi {

    /**
     * 前端请求异常处理接口，100为异常信息，200预留给场景信息，300为上下文信息，400为函数与模型信息
     *
     * @param handlerParameters gql请求参数
     * @param result            程序返回载体
     * @param exception         程序异常
     * @param path              请求路径
     */
    void stackTrace(DataFetcherExceptionHandlerParameters handlerParameters, DataFetcherExceptionHandlerResult result, Throwable exception, ExecutionPath path);

}
