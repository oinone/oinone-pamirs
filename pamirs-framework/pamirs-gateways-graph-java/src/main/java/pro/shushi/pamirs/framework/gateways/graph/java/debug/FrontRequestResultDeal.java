package pro.shushi.pamirs.framework.gateways.graph.java.debug;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;

import java.util.ArrayList;
import java.util.List;


/**
 * 前端请求结果处理接口
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/3 2:41 下午
 */
public interface FrontRequestResultDeal extends Prioritized, CommonApi {

    String DEBUG_KEY = "debug";

    /**
     * 前端请求结果处理接口，200预留给场景信息，300为上下文信息，400为函数与模型信息
     *
     * @param executionResult gql结果
     * @param executionInput  gql参数
     */
    void stackTrace(ExecutionResult executionResult, ExecutionInput executionInput);

    default void addDebugInfo(ExecutionResult executionResult, ClientGraphQLError error) {
        List<ClientGraphQLError> errorList = (List<ClientGraphQLError>) executionResult.getExtensions().get("debug");
        if (errorList == null) {
            errorList = new ArrayList<>();
            executionResult.getExtensions().put(DEBUG_KEY, errorList);
        }
        errorList.add(error);
    }

}
