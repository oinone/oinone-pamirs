package pro.shushi.pamirs.eip.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.eip.api.model.strategy.EipOpenRateLimitPolicy;
import pro.shushi.pamirs.eip.api.pmodel.EipApplicationProxy;
import pro.shushi.pamirs.eip.api.service.model.EipApplicationProxyService;
import pro.shushi.pamirs.eip.api.strategy.service.EipOpenRateLimitPolicyService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.List;

@Component
@Model.model(EipApplicationProxy.MODEL_MODEL)
@UxRouteButton(
        action = @UxAction(name = "EipApplicationCreate", displayName = "创建集成应用", label = "新增应用", contextType = ActionContextTypeEnum.CONTEXT_FREE),
        value = @UxRoute(model = EipApplicationProxy.MODEL_MODEL, viewName = "EipApplicationProxyFormApp", openType = ActionTargetEnum.DRAWER))
@UxRouteButton(
        action = @UxAction(name = "EipApplicationAuth", displayName = "授权调整", label = "授权调整", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = EipApplicationProxy.MODEL_MODEL, viewName = "EipApplicationProxyFormAuth", openType = ActionTargetEnum.DRAWER))
@UxRouteButton(
        action = @UxAction(name = "EipApplicationPrivateDetail", displayName = "查看集成应用密钥", label = "查看密钥", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = EipApplicationProxy.MODEL_MODEL, viewName = "EipApplicationProxyDetail", openType = ActionTargetEnum.DIALOG))
@UxRouteButton(
        action = @UxAction(name = "EipApplicationOperationRateLimit", displayName = "流控配置", label = "流控配置", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = EipApplicationProxy.MODEL_MODEL, viewName = "EipApplicationProxyFormFlowCtrl", openType= ActionTargetEnum.ROUTER))
public class EipApplicationProxyAction {

    @Autowired
    private EipApplicationProxyService eipApplicationProxyService;
    @Autowired
    private EipOpenRateLimitPolicyService eipOpenRateLimitPolicyService;

    @Function.Advanced(type = FunctionTypeEnum.CREATE)
    @Function.fun(FunctionConstants.create)
    @Function(openLevel = {FunctionOpenEnum.API})
    public EipApplicationProxy create(EipApplicationProxy data) {
        return eipApplicationProxyService.create(data);
    }

    @Function.Advanced(type = FunctionTypeEnum.UPDATE)
    @Function.fun(FunctionConstants.update)
    @Function(openLevel = {FunctionOpenEnum.API})
    public EipApplicationProxy update(EipApplicationProxy data) {
        return eipApplicationProxyService.update(data);
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<EipApplicationProxy> queryPage(Pagination<EipApplicationProxy> page, IWrapper<EipApplicationProxy> queryWrapper) {
        return eipApplicationProxyService.queryPage(page, queryWrapper);
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.API})
    public EipApplicationProxy queryOne(EipApplicationProxy query) {
        EipApplicationProxy result = eipApplicationProxyService.queryOne(query);
        if (result != null) {
            Models.data().fieldQuery(result, EipApplicationProxy::getRequestDecryptFunc);
            Models.data().fieldQuery(result, EipApplicationProxy::getResponseEncryptionFunc);
            result.setRateLimitPolicyList(eipOpenRateLimitPolicyService.queryListByApplicationCode(result));
        }
        return result;
    }

    @Action.Advanced(invisible = "!activeRecord.id || !activeRecord.appKey || !activeRecord.authentication || activeRecord.authentication.encryptType != 'RSA'")
    @Action(displayName = "刷新Secret", contextType = ActionContextTypeEnum.SINGLE, bindingType = {ViewTypeEnum.FORM, ViewTypeEnum.DETAIL})
    public EipApplicationProxy regenerateSecret(EipApplicationProxy data) {
        return eipApplicationProxyService.regenerateSecret(data);
    }

    @Action(displayName = "启用", contextType = ActionContextTypeEnum.SINGLE)
    public EipApplicationProxy dataStatusEnable(EipApplicationProxy data) {
        return eipApplicationProxyService.dataStatusEnable(data);
    }

    @Action(displayName = "禁用", contextType = ActionContextTypeEnum.SINGLE)
    public EipApplicationProxy dataStatusDisable(EipApplicationProxy data) {
        return eipApplicationProxyService.dataStatusDisable(data);
    }

    @Action(displayName = "更新流控配置", contextType = ActionContextTypeEnum.SINGLE)
    public EipApplicationProxy updateRateLimitPolicyList(EipApplicationProxy data) {
        List<EipOpenRateLimitPolicy> rateLimitPolicyList = data.getRateLimitPolicyList();
        if (CollectionUtils.isEmpty(rateLimitPolicyList)) {
            eipOpenRateLimitPolicyService.removeAll(data);
        } else {
            eipOpenRateLimitPolicyService.batchModifyFlowControlPolicies(rateLimitPolicyList);
        }
        return data;
    }
}
