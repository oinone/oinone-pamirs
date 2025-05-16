package pro.shushi.pamirs.eip.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.eip.api.model.EipConnGroup;
import pro.shushi.pamirs.eip.api.service.model.EipConnGroupService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;

@Component
@Model.model(EipConnGroup.MODEL_MODEL)
@UxRouteButton(
        action = @UxAction(name = "EipConnGroupCreate", displayName = "新增业务域", label = "新增业务域", contextType = ActionContextTypeEnum.CONTEXT_FREE),
        value = @UxRoute(model = EipConnGroup.MODEL_MODEL, viewName = "EipConnGroupForm", openType = ActionTargetEnum.DRAWER))
public class EipConnGroupAction {

    @Autowired
    private EipConnGroupService eipConnGroupService;

    @Function.Advanced(type = FunctionTypeEnum.CREATE)
    @Function.fun(FunctionConstants.create)
    @Function(openLevel = {FunctionOpenEnum.API})
    public EipConnGroup create(EipConnGroup data) {
        return eipConnGroupService.create(data);
    }

    @Function.Advanced(type = FunctionTypeEnum.DELETE, managed = true)
    @Function.fun(FunctionConstants.deleteWithFieldBatch)
    @Function(openLevel = {FunctionOpenEnum.API, FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public List<EipConnGroup> delete(List<EipConnGroup> list) {
        return eipConnGroupService.delete(list);
    }

    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE)
    public EipConnGroup deleteOne(EipConnGroup data) {
        return eipConnGroupService.deleteOne(data);
    }

}
