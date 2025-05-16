package pro.shushi.pamirs.eip.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.eip.api.service.EipService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.core.common.behavior.impl.DataStatusBehavior;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;
import pro.shushi.pamirs.core.common.FetchUtil;

@Component
@Model.model(EipRouteDefinition.MODEL_MODEL)
public class EipRouteDefinitionAction extends DataStatusBehavior<EipRouteDefinition> {

    @Autowired
    private EipService eipService;

    @Override
    protected EipRouteDefinition fetchData(EipRouteDefinition data) {
        data = FetchUtil.fetchOne(data);
        if (data == null) {
            throw PamirsException.construct(ExpEnumerate.SELECT_NULL).errThrow();
        }
        return data;
    }

    @Override
    @Transactional
    @Action.Advanced(invisible = "(!context.activeRecord.isDBManaged || context.activeRecord.dataStatus != 'NOT_ENABLED') && context.activeRecord.dataStatus != 'DISABLED'")
    @Action(displayName = "启用", contextType = ActionContextTypeEnum.SINGLE)
    public EipRouteDefinition dataStatusEnable(EipRouteDefinition data) {
        data = super.dataStatusEnable(data);
        data.updateById();
        eipService.registerRouteDefinition(data);
        return data;
    }

    @Override
    @Transactional
    @Action.Advanced(invisible = "!context.activeRecord.isDBManaged || context.activeRecord.dataStatus != 'ENABLED'")
    @Action(displayName = "禁用", contextType = ActionContextTypeEnum.SINGLE)
    public EipRouteDefinition dataStatusDisable(EipRouteDefinition data) {
        data = super.dataStatusDisable(data);
        data.updateById();
        eipService.cancellationRouteDefinition(data);
        return data;
    }
}
