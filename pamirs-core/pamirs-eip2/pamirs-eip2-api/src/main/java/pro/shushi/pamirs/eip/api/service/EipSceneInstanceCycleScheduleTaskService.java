package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.model.scene.EipSceneInstance;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleAction;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

/**
 * @author drome
 * @date 2021/8/27:57 下午
 */
@Fun(EipSceneInstanceCycleScheduleTaskService.FUN_NAMESPACE)
public interface EipSceneInstanceCycleScheduleTaskService extends ScheduleAction {

    String FUN_NAMESPACE = "pamirs.eip.EipSceneInstanceCycleScheduleTaskService";

    @Override
    default String getInterfaceName() {
        return FUN_NAMESPACE;
    }

    @Function
    @Override
    Result<Void> execute(ScheduleItem task);

    @Function
    Boolean initTask(EipSceneInstance eipSceneInstance);

    @Function
    Boolean cancelTask(EipSceneInstance eipSceneInstance);

    @Function
    ScheduleItem queryScheduleItemByInstance(EipSceneInstance eipSceneInstance);

}
