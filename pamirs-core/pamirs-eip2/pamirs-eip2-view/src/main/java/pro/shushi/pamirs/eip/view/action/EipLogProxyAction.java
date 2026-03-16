package pro.shushi.pamirs.eip.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.eip.api.pmodel.EipLogProxy;
import pro.shushi.pamirs.eip.api.service.model.EipLogProxyService;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

@Component
@Model.model(EipLogProxy.MODEL_MODEL)
@UxRouteButton(
        action = @UxAction(name = "EipLogDetail", displayName = "接口日志详情", label = "详情", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = EipLogProxy.MODEL_MODEL, viewName = "EipLogProxyProxyDetail", openType = ActionTargetEnum.DRAWER))
public class EipLogProxyAction {

    @Autowired
    private EipLogProxyService eipLogProxyService;

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<EipLogProxy> queryPage(Pagination<EipLogProxy> page, IWrapper<EipLogProxy> queryWrapper) {
        return eipLogProxyService.queryPage(page, queryWrapper);
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.API})
    public EipLogProxy queryOne(EipLogProxy query) {
        return eipLogProxyService.queryOne(query);
    }
}
