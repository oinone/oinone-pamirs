package pro.shushi.pamirs.trigger.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleAction;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

/**
 * @author Adamancy Zhang
 * @date 2020-12-02 14:57
 */
@Fun(TriggerTaskActionExecutor.FUN_NAMESPACE)
public interface TriggerTaskActionExecutor extends ScheduleAction {

    String FUN_NAMESPACE = "pro.shushi.pamirs.trigger.service.TriggerTaskActionExecutor";

    @Override
    default String getInterfaceName() {
        return FUN_NAMESPACE;
    }

    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    @Override
    Result<Void> execute(ScheduleItem task);
}
