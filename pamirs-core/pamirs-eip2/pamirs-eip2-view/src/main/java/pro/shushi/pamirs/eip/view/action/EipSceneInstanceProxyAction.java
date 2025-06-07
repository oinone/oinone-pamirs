package pro.shushi.pamirs.eip.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.EipIncUpdateCursor;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneInstance;
import pro.shushi.pamirs.eip.api.pmodel.EipSceneInstanceProxy;
import pro.shushi.pamirs.eip.api.service.EipSceneInstanceCycleScheduleTaskService;
import pro.shushi.pamirs.eip.api.service.model.EipIncUpdateCursorService;
import pro.shushi.pamirs.eip.api.service.model.EipSceneInstanceService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

import java.util.Date;

@Component
@Model.model(EipSceneInstanceProxy.MODEL_MODEL)
public class EipSceneInstanceProxyAction {

    @Autowired
    private EipSceneInstanceService eipSceneInstanceService;
    @Autowired
    private EipIncUpdateCursorService eipIncUpdateLogService;
    @Autowired
    private EipSceneInstanceCycleScheduleTaskService eipSceneInstanceCycleScheduleTaskService;

    @Function(openLevel = FunctionOpenEnum.API, summary = "集成场景实例构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public EipSceneInstanceProxy construct(EipSceneInstanceProxy data, EipSceneInstance source) {
        data = new EipSceneInstanceProxy().queryById(source.getId());
        return _fullProxy(data);
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.API})
    public EipSceneInstanceProxy queryOne(EipSceneInstanceProxy data) {
        data = data.queryOne();
        return _fullProxy(data);
    }

    private EipSceneInstanceProxy _fullProxy(EipSceneInstanceProxy proxy) {
//        schedule执行信息
        ScheduleItem scheduleItem = eipSceneInstanceCycleScheduleTaskService.queryScheduleItemByInstance(proxy);
        if (scheduleItem != null) {
            proxy.setScheduleNextExecuteTime(scheduleItem.getNextExecuteTime() == null ? null : new Date(scheduleItem.getNextExecuteTime()));
            proxy.setScheduleLastExecuteTime(scheduleItem.getLastExecuteTime() == null ? null : new Date(scheduleItem.getLastExecuteTime()));
            proxy.setScheduleExecuteNumber(scheduleItem.getExecuteNumber());
        }

//        增量执行信息
        EipIncUpdateCursor incUpdateLog = eipIncUpdateLogService.queryByInterfaceName(proxy.getIncUpdateLogInterfaceName());
        if (incUpdateLog != null) {
            proxy.setIncUpdateLogStartTime(incUpdateLog.getStartTime());
            proxy.setIncUpdateLogEndTime(incUpdateLog.getEndTime());
            proxy.setIncUpdateLogParams(incUpdateLog.getParams());
            proxy.setIncUpdateLogLastUpdateTime(incUpdateLog.getLastUpdateTime());
        }
        return proxy;
    }

    @Action(displayName = "确定", summary = "修改定时任务配置", bindingType = ViewTypeEnum.FORM)
    public EipSceneInstanceProxy updateTask(EipSceneInstanceProxy data) {
        eipSceneInstanceService.updateTask(data);
        return data;
    }

    @Action(displayName = "确定", summary = "修改增量日志配置", bindingType = ViewTypeEnum.FORM)
    public EipSceneInstanceProxy updateIncUpdateLog(EipSceneInstanceProxy data) {
        eipSceneInstanceService.updateIncUpdateLog(data);
        return data;
    }

    @Action(displayName = "确定", summary = "修改增量日志配置", bindingType = ViewTypeEnum.FORM)
    public EipSceneInstanceProxy testCall(EipSceneInstanceProxy data) {
        eipSceneInstanceService.testCall(data);
        return data;
    }
}
