package pro.shushi.pamirs.eip.view.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.eip.api.model.strategy.EipOpenRateLimitPolicy;
import pro.shushi.pamirs.eip.api.strategy.service.EipOpenRateLimitPolicyService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @author yeshenyue on 2025/4/21 19:23.
 */
@Component
@Model.model(EipOpenRateLimitPolicy.MODEL_MODEL)
@UxRouteButton(
        action = @UxAction(name = "BatchUpdateRateLimit", displayName = "批量更新流控策略", label = "批量更新流控策略", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH),
        value = @UxRoute(model = EipOpenRateLimitPolicy.MODEL_MODEL, viewName = "批量更新流控策略from", openType= ActionTargetEnum.DIALOG))
@UxRouteButton(
        action = @UxAction(name = "EditOpenRateLimitPolicy", displayName = "编辑流控策略", label = "编辑流控策略", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = EipOpenRateLimitPolicy.MODEL_MODEL, viewName = "编辑流控策略from", openType= ActionTargetEnum.DIALOG))
public class EipOpenRateLimitPolicyAction {

    @Resource
    private EipOpenRateLimitPolicyService eipOpenRateLimitPolicyService;

    @Action.Advanced(name = FunctionConstants.createBatch)
    @Action(displayName = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.createBatch)
    @Function.fun(FunctionConstants.createBatch)
    public List<EipOpenRateLimitPolicy> createBatch(List<EipOpenRateLimitPolicy> data) {
        return eipOpenRateLimitPolicyService.batchModifyFlowControlPolicies(data);
    }
}
