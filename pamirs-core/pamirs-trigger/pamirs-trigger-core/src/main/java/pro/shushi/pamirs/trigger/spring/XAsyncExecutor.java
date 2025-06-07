package pro.shushi.pamirs.trigger.spring;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleAction;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

/**
 * @author Adamancy Zhang
 * @date 2020-12-02 14:53
 */
@Fun(XAsyncExecutor.FUN_NAMESPACE)
public interface XAsyncExecutor extends ScheduleAction {

    String FUN_NAMESPACE = "pro.shushi.pamirs.trigger.spring.XAsyncExecutor";

    String EXECUTE_INTERFACE_METHOD_NAME = "executeExecuteTaskAction";

    Result<Void> executeExecuteTaskAction(ScheduleItem task);

    @Override
    default String getInterfaceName() {
        return FUN_NAMESPACE;
    }

    @Override
    default String getMethodName() {
        return EXECUTE_INTERFACE_METHOD_NAME;
    }

    @Override
    default Result<Void> execute(ScheduleItem task) {
        return executeExecuteTaskAction(task);
    }
}
