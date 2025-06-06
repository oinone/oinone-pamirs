package pro.shushi.pamirs.eip.view.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.pmodel.EipOpenIpBlacklistProxy;
import pro.shushi.pamirs.eip.api.util.EipIpUtil;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

/**
 * @author yeshenyue on 2025/5/9 17:52.
 */
@Component
@Model.model(EipOpenIpBlacklistProxy.MODEL_MODEL)
@UxRouteButton(
        action = @UxAction(name = "EipOpenIpBlacklistCreate", displayName = "创建", label = "创建", contextType = ActionContextTypeEnum.CONTEXT_FREE),
        value = @UxRoute(model = EipOpenIpBlacklistProxy.MODEL_MODEL, viewName = "开放应用ip黑名单create", openType = ActionTargetEnum.DIALOG))
@UxRouteButton(
        action = @UxAction(name = "EipOpenIpBlacklistEdit", displayName = "编辑", label = "编辑", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = EipOpenIpBlacklistProxy.MODEL_MODEL, viewName = "开放应用ip黑名单edit", openType = ActionTargetEnum.DIALOG))
public class EipOpenIpBlacklistProxyAction {

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.ip)", error = "IP/IP网段不允许为空"),
            @Validation.Rule(value = "!IS_BLANK(data.applicationCode)", error = "开放应用不允许为空"),
    })
    @Action.Advanced(name = FunctionConstants.create)
    @Action(displayName = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public EipOpenIpBlacklistProxy create(EipOpenIpBlacklistProxy data) {
        checkData(data);
        return data.create();
    }

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.ip)", error = "IP/IP网段不允许为空"),
            @Validation.Rule(value = "!IS_BLANK(data.applicationCode)", error = "开放应用不允许为空"),
    })
    @Action.Advanced(name = FunctionConstants.update)
    @Action(displayName = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public EipOpenIpBlacklistProxy update(EipOpenIpBlacklistProxy data) {
        checkData(data);
        data.updateById();
        return data;
    }

    private void checkData(EipOpenIpBlacklistProxy data) {
        if (EipIpUtil.isIllegalIp(data.getIp())) {
            throw PamirsException.construct(EipExpEnumerate.EIP_IP_CIDR_ILLEGAL).errThrow();
        }
        if (data.getHttpCode() != null && (data.getHttpCode() < 100 || data.getHttpCode() > 599)) {
            throw PamirsException.construct(EipExpEnumerate.EIP_HTTP_CODE_VALUE_ILLEGAL).errThrow();
        }
    }
}
