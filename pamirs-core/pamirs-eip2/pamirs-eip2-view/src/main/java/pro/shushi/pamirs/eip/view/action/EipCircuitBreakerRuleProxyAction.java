package pro.shushi.pamirs.eip.view.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.eip.api.pmodel.EipCircuitBreakerRuleProxy;
import pro.shushi.pamirs.eip.api.service.EipCircuitBreakerRuleProxyService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import javax.annotation.Resource;

/**
 * @author yeshenyue on 2025/4/14 15:20.
 */
@Component
@Model.model(EipCircuitBreakerRuleProxy.MODEL_MODEL)
@UxRouteButton(
        action = @UxAction(name = "熔断配置create", displayName = "创建", label = "创建", contextType = ActionContextTypeEnum.CONTEXT_FREE),
        value = @UxRoute(model = EipCircuitBreakerRuleProxy.MODEL_MODEL, viewName = "熔断配置create", openType = ActionTargetEnum.DIALOG))
@UxRouteButton(
        action = @UxAction(name = "熔断配置edit", displayName = "更新", label = "更新", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = EipCircuitBreakerRuleProxy.MODEL_MODEL, viewName = "熔断配置edit", openType = ActionTargetEnum.DIALOG))
public class EipCircuitBreakerRuleProxyAction {

    @Resource
    private EipCircuitBreakerRuleProxyService eipCircuitBreakerRuleProxyService;

    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE)
    public EipCircuitBreakerRuleProxy deleteOne(EipCircuitBreakerRuleProxy data) {
        eipCircuitBreakerRuleProxyService.deleteOne(data);
        return data;
    }

    @Action.Advanced(name = FunctionConstants.create)
    @Action(displayName = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public EipCircuitBreakerRuleProxy create(EipCircuitBreakerRuleProxy data) {
        return eipCircuitBreakerRuleProxyService.create(data);
    }

    @Action.Advanced(name = FunctionConstants.update)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public EipCircuitBreakerRuleProxy update(EipCircuitBreakerRuleProxy data) {
        eipCircuitBreakerRuleProxyService.update(data);
        return data;
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<EipCircuitBreakerRuleProxy> queryPage(Pagination<EipCircuitBreakerRuleProxy> page, IWrapper<EipCircuitBreakerRuleProxy> queryWrapper) {
        return eipCircuitBreakerRuleProxyService.queryPage(page, queryWrapper);
    }
}
