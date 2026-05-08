package pro.shushi.pamirs.eip.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.eip.api.pmodel.EipLogProxy;
import pro.shushi.pamirs.eip.api.service.EipLogRetryService;
import pro.shushi.pamirs.eip.api.service.model.EipLogProxyService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Model.model(EipLogProxy.MODEL_MODEL)
@UxRouteButton(
        action = @UxAction(name = "EipLogDetail", displayName = "接口日志详情", label = "详情", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = EipLogProxy.MODEL_MODEL, viewName = "EipLogProxyDetail", openType = ActionTargetEnum.DRAWER))
public class EipLogProxyAction {

    @Autowired
    private EipLogProxyService eipLogProxyService;

    @Autowired
    private EipLogRetryService eipLogRetryService;

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

    @Action(displayName = "重试")
    public EipLogProxy retry(EipLogProxy data) {
        eipLogRetryService.retryOne(data.getId());
        return data;
    }

    @Action(displayName = "重试", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    public List<EipLogProxy> batchRetry(List<EipLogProxy> dataList) {
        List<Long> logIds = dataList.stream().map(EipLogProxy::getId).collect(Collectors.toList());
        eipLogRetryService.retryBatch(logIds);
        return dataList;
    }
}
