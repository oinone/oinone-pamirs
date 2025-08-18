package pro.shushi.pamirs.eip.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.behavior.impl.DataStatusBehavior;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogStrategyService;
import pro.shushi.pamirs.eip.api.service.EipService;
import pro.shushi.pamirs.eip.api.service.model.EipIntegrationInterfaceService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

@Component
@Model.model(EipIntegrationInterface.MODEL_MODEL)
public class EipIntegrationInterfaceAction extends DataStatusBehavior<EipIntegrationInterface> {

    @Autowired
    private EipService eipService;

    @Autowired
    private EipLogStrategyService eipLogStrategyService;

    @Autowired
    private EipIntegrationInterfaceService eipIntegrationInterfaceService;

    public static EipIntegrationInterface fetchIntegrationInterface(EipIntegrationInterface data) {
        data = FetchUtil.fetchOne(data);
        if (data == null)
            throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).errThrow();
        return data;
    }

    @Override
    protected EipIntegrationInterface fetchData(EipIntegrationInterface data) {
        return fetchIntegrationInterface(data);
    }

    @Override
    @Transactional
    @Action.Advanced(invisible = "(!context.activeRecord.isDBManaged || context.activeRecord.dataStatus != 'NOT_ENABLED') && context.activeRecord.dataStatus != 'DISABLED'")
    @Action(displayName = "发布", contextType = ActionContextTypeEnum.SINGLE, bindingType = {ViewTypeEnum.TABLE})
    public EipIntegrationInterface dataStatusEnable(EipIntegrationInterface data) {
        data = super.dataStatusEnable(data);
        data.updateById();
        eipService.registerInterface(data);
        return (EipIntegrationInterface) new EipIntegrationInterface()
                .setUri(data.getUri())
                .setInterfaceName(data.getInterfaceName())
                .setExchangePattern(data.getExchangePattern())
                .setModuleDefinition(data.getModuleDefinition())
                .setDataStatus(data.getDataStatus())
                .setName(data.getName())
                .setId(data.getId())
                .setCreateUid(data.getCreateUid())
                .setCreateDate(data.getCreateDate())
                .setWriteUid(data.getWriteUid())
                .setWriteDate(data.getWriteDate());
    }

    @Override
    @Transactional
    @Action.Advanced(invisible = "!context.activeRecord.isDBManaged || context.activeRecord.dataStatus != 'ENABLED'")
    @Action(displayName = "禁用", contextType = ActionContextTypeEnum.SINGLE, bindingType = {ViewTypeEnum.TABLE})
    public EipIntegrationInterface dataStatusDisable(EipIntegrationInterface data) {
        data = super.dataStatusDisable(data);
        data.updateById();
        eipService.cancellationInterface(data);
        return data;
    }

    @Transactional
    @Action.Advanced(invisible = "context.activeRecord.isIgnoreLogFrequency == false")
    @Action(displayName = "忽略日志记录频率限制", contextType = ActionContextTypeEnum.SINGLE, bindingType = {ViewTypeEnum.TABLE})
    public EipIntegrationInterface ignoreLogFrequency(EipIntegrationInterface data) {
        data = data.queryById();
        eipLogStrategyService.ignoreFrequency(data.getInterfaceName(), InterfaceTypeEnum.INTEGRATION);
        data.setIsIgnoreLogFrequency(true);
        if (DataStatusEnum.ENABLED.equals(data.getDataStatus())) {
            eipService.registerInterface(data);
        }
        return data;
    }

    @Transactional
    @Action.Advanced(invisible = "context.activeRecord.isIgnoreLogFrequency == true")
    @Action(displayName = "取消忽略日志记录频率限制", contextType = ActionContextTypeEnum.SINGLE, bindingType = {ViewTypeEnum.TABLE})
    public EipIntegrationInterface cancelIgnoreLogFrequency(EipIntegrationInterface data) {
        data = data.queryById();
        eipLogStrategyService.cancelIgnoreFrequency(data.getInterfaceName(), InterfaceTypeEnum.INTEGRATION);
        data.setIsIgnoreLogFrequency(false);
        if (DataStatusEnum.ENABLED.equals(data.getDataStatus())) {
            eipService.registerInterface(data);
        }
        return data;
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<EipIntegrationInterface> queryPage(Pagination<EipIntegrationInterface> page, IWrapper<EipIntegrationInterface> queryWrapper) {
        return eipIntegrationInterfaceService.queryPage(page, queryWrapper);
    }
}
