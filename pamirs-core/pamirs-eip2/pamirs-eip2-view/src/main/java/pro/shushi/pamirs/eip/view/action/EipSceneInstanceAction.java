package pro.shushi.pamirs.eip.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneInstance;
import pro.shushi.pamirs.eip.api.service.model.EipSceneInstanceService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

@Component
@Model.model(EipSceneInstance.MODEL_MODEL)
public class EipSceneInstanceAction {

    @Autowired
    private EipSceneInstanceService eipSceneInstanceService;

    @Action.Advanced(name = FunctionConstants.create, managed = true)
    @Action(displayName = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public EipSceneInstance create(EipSceneInstance data) {
        eipSceneInstanceService.create(data);
        return data;
    }

    @Action.Advanced(invisible = "context.activeRecord.dataStatus != 'NOT_ENABLED' && context.activeRecord.dataStatus != 'DISABLED'")
    @Action(displayName = "启用", contextType = ActionContextTypeEnum.SINGLE)
    public EipSceneInstance dataStatusEnable(EipSceneInstance data) {
        eipSceneInstanceService.enable(data);
        return data;
    }

    @Action.Advanced(invisible = "context.activeRecord.dataStatus != 'ENABLED'")
    @Action(displayName = "禁用", contextType = ActionContextTypeEnum.SINGLE)
    public EipSceneInstance dataStatusDisable(EipSceneInstance data) {
        eipSceneInstanceService.disable(data);
        return data;
    }
}
