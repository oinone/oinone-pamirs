package pro.shushi.pamirs.framework.common.api;

import graphql.ExecutionInput;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;

/**
 * 前端请求初始化接口
 *
 * @author Adamancy Zhang at 16:47 on 2024-05-20
 */
public interface FrontRequestInitDeal extends SessionClearApi, Prioritized, CommonApi {

    /**
     * GQL入口初始化
     *
     * @param executionInput GQL
     */
    void init(ExecutionInput executionInput);

    /**
     * 远程调用入口初始化
     *
     * @param invoker    调用器
     * @param invocation 调用信息
     */
    void init(Invoker<?> invoker, Invocation invocation);

    @Override
    default int priority() {
        return 0;
    }

}
