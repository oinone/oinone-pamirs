package pro.shushi.pamirs.eip.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.eip.api.pmodel.alarm.EipAlarmRuleProxy;
import pro.shushi.pamirs.eip.api.service.alarm.EipAlarmRuleProxyService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

/**
 * EipAlarmRuleProxyAction
 *
 * @author yakir on 2026/04/07 11:47.
 */
@Slf4j
@Component
@Model.model(EipAlarmRuleProxy.MODEL_MODEL)
@UxRouteButton(
        action = @UxAction(name = "redirectCreatePage", displayName = "创建", label = "创建", contextType = ActionContextTypeEnum.CONTEXT_FREE),
        value = @UxRoute(model = EipAlarmRuleProxy.MODEL_MODEL, viewName = "formView", openType = ActionTargetEnum.DIALOG))
@UxRouteButton(
        action = @UxAction(name = "redirectUpdatePage", displayName = "编辑", label = "编辑", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = EipAlarmRuleProxy.MODEL_MODEL, viewName = "formView", openType = ActionTargetEnum.DIALOG))
@UxRouteButton(
        action = @UxAction(name = "redirectDetailPage", displayName = "详情", label = "详情", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = EipAlarmRuleProxy.MODEL_MODEL, viewName = "detailView", openType = ActionTargetEnum.DIALOG))
public class EipAlarmRuleProxyAction {

    @Autowired
    private EipAlarmRuleProxyService eipAlarmRuleProxyService;

    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    @Action(displayName = "创建/更新", label = "确定", bindingType = ViewTypeEnum.FORM)
    @Action.Advanced(name = FunctionConstants.create, type = FunctionTypeEnum.CREATE)
    public EipAlarmRuleProxy create(EipAlarmRuleProxy data) {

        return eipAlarmRuleProxyService.createOrUpdate(data);
    }

    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    @Action(displayName = "更新", label = "确定", bindingType = ViewTypeEnum.FORM)
    @Action.Advanced(name = FunctionConstants.update, type = FunctionTypeEnum.UPDATE)
    public EipAlarmRuleProxy update(EipAlarmRuleProxy data) {

        return eipAlarmRuleProxyService.createOrUpdate(data);
    }

    @Function(openLevel = {FunctionOpenEnum.API})
    @Function.fun(FunctionConstants.queryByEntity)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public EipAlarmRuleProxy queryOne(EipAlarmRuleProxy query) {

        return eipAlarmRuleProxyService.queryOne(query);
    }

    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    @Function.fun(FunctionConstants.queryPage)
    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    public Pagination<EipAlarmRuleProxy> queryPage(Pagination<EipAlarmRuleProxy> page, IWrapper<EipAlarmRuleProxy> queryWrapper) {

        return eipAlarmRuleProxyService.queryPage(page, queryWrapper);
    }

    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE)
    public EipAlarmRuleProxy deleteOne(EipAlarmRuleProxy data) {

        return eipAlarmRuleProxyService.deleteOne(data);
    }

}
