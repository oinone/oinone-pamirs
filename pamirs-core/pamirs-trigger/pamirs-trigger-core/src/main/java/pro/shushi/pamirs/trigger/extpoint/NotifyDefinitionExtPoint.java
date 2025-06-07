package pro.shushi.pamirs.trigger.extpoint;

import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.extpoint.DefaultReadWriteExtPoint;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.trigger.enmu.TriggerExpEnumerate;
import pro.shushi.pamirs.trigger.model.NotifyDefinition;
import pro.shushi.pamirs.trigger.model.NotifyFunctionDefinition;
import pro.shushi.pamirs.trigger.notify.NotifyContext;

@Base
@Ext(NotifyDefinition.class)
public class NotifyDefinitionExtPoint extends DefaultReadWriteExtPoint<NotifyDefinition> {

    /**
     * 创建前校验选择函数有效性，更新前采用相同逻辑
     */
    @Override
    @ExtPoint.Implement(priority = 999)
    public NotifyDefinition createBefore(NotifyDefinition data) {
        Function function = PamirsSession.getContext().getFunction(data.getExecuteNamespace(), data.getExecuteFun());
        if (function == null)
            throw PamirsException.construct(TriggerExpEnumerate.FUNCTION_NOT_FOUND).errThrow();
        if (data.getTargetFunctionList() == null || data.getTargetFunctionList().isEmpty())
            throw PamirsException.construct(TriggerExpEnumerate.TARGET_FUNCTION_LIST_NULL).errThrow();
        for (NotifyFunctionDefinition targetFunctionDefinition : data.getTargetFunctionList()) {
            function = PamirsSession.getContext().getFunction(targetFunctionDefinition.getTargetNamespace(), targetFunctionDefinition.getTargetFun());
            if (function == null)
                throw PamirsException.construct(TriggerExpEnumerate.TARGET_FUNCTION_NOT_FOUND).errThrow();
        }
        NotifyDefinition originNotifyDefinition = NotifyContext.getNotifyDefinition(data.getExecuteNamespace(), data.getExecuteFun());
        if (originNotifyDefinition != null)
            throw PamirsException.construct(TriggerExpEnumerate.NOTIFY_DEFINITION_IS_EXIST).errThrow();
        return data;
    }

    @Override
    @ExtPoint.Implement(priority = 999)
    public NotifyDefinition createAfter(NotifyDefinition data) {
        NotifyContext.putNotifyDefinition(data.getExecuteNamespace(), data.getExecuteFun(), data);
        return data;
    }

    @Override
    @ExtPoint.Implement(priority = 999)
    public NotifyDefinition updateBefore(NotifyDefinition data) {
        createBefore(data);
        NotifyDefinition notifyDefinition = new NotifyDefinition().setId(data.getId()).queryById();
        //移除缓存中被修改的执行函数消息通知
        if (!notifyDefinition.getExecuteNamespace().equals(data.getExecuteNamespace()) || !notifyDefinition.getExecuteFun().equals(data.getExecuteFun()))
            NotifyContext.removeNotifyDefinition(notifyDefinition.getExecuteNamespace(), notifyDefinition.getExecuteFun());
        return data;
    }

    @Override
    @ExtPoint.Implement(priority = 999)
    public NotifyDefinition updateAfter(NotifyDefinition data) {
        NotifyContext.putNotifyDefinition(data.getExecuteNamespace(), data.getExecuteFun(), data);
        return data;
    }
}
