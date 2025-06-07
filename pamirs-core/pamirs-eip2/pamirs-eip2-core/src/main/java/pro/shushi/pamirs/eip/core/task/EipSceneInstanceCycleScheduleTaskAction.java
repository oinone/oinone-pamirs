package pro.shushi.pamirs.eip.core.task;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.constant.EipSceneConstant;
import pro.shushi.pamirs.eip.api.executor.EipSceneInstanceExecutor;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneInstance;
import pro.shushi.pamirs.eip.api.service.EipSceneInstanceCycleScheduleTaskService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskType;
import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;
import pro.shushi.pamirs.trigger.service.ScheduleOperateService;
import pro.shushi.pamirs.trigger.service.ScheduleTaskActionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.common.util.TypeReferences.TR_MAP_SS;

/**
 * @author drome
 * @date 2021/8/27:53 下午
 */
@Slf4j
@Component
@Fun(EipSceneInstanceCycleScheduleTaskService.FUN_NAMESPACE)
public class EipSceneInstanceCycleScheduleTaskAction implements EipSceneInstanceCycleScheduleTaskService {

    //    2022年10月24日11:24:12 要满足eip只依赖core就可以获得集成和开放相关能力, 不一定有调度能力
    @Autowired(required = false)
    private ScheduleTaskActionService scheduleTaskActionService;

    @Autowired(required = false)
    private ScheduleOperateService scheduleOperateService;

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function(summary = "场景实例定时执行", name = DEFAULT_METHOD)
    @Override
    public Result<Void> execute(ScheduleItem scheduleItem) {
        Map<String, String> context = JSON.parseObject(scheduleItem.getContext(), TR_MAP_SS);
        Object scheduleContext = null;
        if (context != null) {
            scheduleContext = context.get(EipSceneConstant.SCHEDULE_INSTANCE_BODY);
        }
        //根据schedule任务入参执行实例
        EipSceneInstanceExecutor
                .newInstance(null)
                .call(scheduleItem.getBizCode(), scheduleContext);
        return new Result<>();
    }

    @Function
    @Override
    public Boolean initTask(EipSceneInstance eipSceneInstance) {
        ScheduleTaskAction scheduleTaskAction = new ScheduleTaskAction();
        scheduleTaskAction.setTechnicalName(eipSceneInstance.getScheduleTechnicalName());

        //默认值
        scheduleTaskAction
                .setBizId(eipSceneInstance.getId())
                .setBizCode(eipSceneInstance.getCode())
                .setTaskType(TaskType.CYCLE_SCHEDULE_NO_TRANSACTION_TASK.getValue())
                .setDisplayName(eipSceneInstance.getName())
                .setExecuteNamespace(getInterfaceName())
                .setExecuteFun(getMethodName())
                .setExecuteFunction(new FunctionDefinition().setTimeout(-1))
                .setActive(Boolean.TRUE);

        //根据实例配置初始化
        scheduleTaskAction
                .setPeriodTimeUnit(eipSceneInstance.getPeriodTimeUnit())
                .setPeriodTimeValue(eipSceneInstance.getPeriodTimeValue())
                .setNextRetryTimeValue(eipSceneInstance.getNextRetryTimeValue())
                .setNextRetryTimeUnit(eipSceneInstance.getNextRetryTimeUnit())
                .setLimitRetryNumber(eipSceneInstance.getLimitRetryNumber());

        //上下文
        Map<String, String> context = new HashMap<>();
        context.put(EipSceneConstant.SCHEDULE_INSTANCE_BODY, eipSceneInstance.getInstanceBody());
        scheduleTaskAction.setContext(JSON.toJSONString(context));
        //生效的情况下,编辑参数,所以要调submit. 取消的情况下重新启用,所以要调active. submit不会修改isCancel
        scheduleTaskActionService.submit(scheduleTaskAction);
        scheduleTaskActionService.active(scheduleTaskAction.getTechnicalName());
        return Boolean.TRUE;
    }

    @Function
    @Override
    public Boolean cancelTask(EipSceneInstance eipSceneInstance) {
        return scheduleTaskActionService.cancel(eipSceneInstance.getScheduleTechnicalName());
    }

    @Function
    @Override
    public ScheduleItem queryScheduleItemByInstance(EipSceneInstance eipSceneInstance) {
        // 内置了userId条件,自己创建的任务才可见
        List<ScheduleItem> scheduleItems = scheduleOperateService.selectListByEntity(
                new ScheduleItem()
                        .setTechnicalName(eipSceneInstance.getScheduleTechnicalName())
                        .setTaskType(TaskType.CYCLE_SCHEDULE_NO_TRANSACTION_TASK.getValue())
        );
        if (CollectionUtils.isEmpty(scheduleItems)) {
            return null;
        }
        return scheduleItems.get(0);
    }
}
