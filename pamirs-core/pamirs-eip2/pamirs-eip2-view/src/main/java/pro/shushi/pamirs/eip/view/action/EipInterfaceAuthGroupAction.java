package pro.shushi.pamirs.eip.view.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.behavior.impl.DataStatusBehavior;
import pro.shushi.pamirs.eip.api.model.EipInterfaceAuthGroup;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;

@Model.model(EipInterfaceAuthGroup.MODEL_MODEL)
@Component
public class EipInterfaceAuthGroupAction extends DataStatusBehavior<EipInterfaceAuthGroup> {

    @Override
    protected EipInterfaceAuthGroup fetchData(EipInterfaceAuthGroup data) {
        EipInterfaceAuthGroup target = FetchUtil.fetchOne(data);
        if (target == null)
            throw PamirsException.construct(ExpEnumerate.SELECT_NULL).errThrow();
        return target;
    }

    @Override
    @Action.Advanced(invisible = "context.activeRecord.dataStatus != 'NOT_ENABLED' && context.activeRecord.dataStatus != 'DISABLED'")
    @Action(displayName = "启用", contextType = ActionContextTypeEnum.SINGLE)
    public EipInterfaceAuthGroup dataStatusEnable(EipInterfaceAuthGroup data) {
        data = super.dataStatusEnable(data);
        data.updateById();
        return data;
    }

    @Override
    @Action.Advanced(invisible = "context.activeRecord.dataStatus != 'ENABLED'")
    @Action(displayName = "禁用", contextType = ActionContextTypeEnum.SINGLE)
    public EipInterfaceAuthGroup dataStatusDisable(EipInterfaceAuthGroup data) {
        data = super.dataStatusDisable(data);
        data.updateById();
        return data;
    }

}
