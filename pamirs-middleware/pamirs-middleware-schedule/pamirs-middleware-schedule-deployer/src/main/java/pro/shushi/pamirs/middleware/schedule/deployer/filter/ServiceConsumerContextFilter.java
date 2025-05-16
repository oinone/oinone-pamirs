package pro.shushi.pamirs.middleware.schedule.deployer.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import pro.shushi.pamirs.middleware.schedule.deployer.session.ScheduleRpcSession;

/**
 * 服务消费者拦截
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Activate(group = CommonConstants.CONSUMER)
public class ServiceConsumerContextFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            RpcContext.getContext().setAttachments(ScheduleRpcSession.fetchSessionMap());
            return invoker.invoke(invocation);
        } finally {
            //清理
            RpcContext.getContext().clearAttachments();
        }
    }

}