package pro.shushi.pamirs.eip.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.service.EipService;
import pro.shushi.pamirs.eip.api.service.model.EipOpenInterfaceService;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogStrategyService;
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

@Component
@Model.model(EipOpenInterface.MODEL_MODEL)
public class EipOpenInterfaceAction {

    @Autowired
    private EipOpenInterfaceService eipOpenInterfaceService;

    @Autowired
    private EipLogStrategyService eipLogStrategyService;

    @Autowired
    private EipService eipService;

    @Action(displayName = "启用", contextType = ActionContextTypeEnum.SINGLE)
    public EipOpenInterface dataStatusEnable(EipOpenInterface data) {
        eipOpenInterfaceService.enable(data);
        return new EipOpenInterface().queryById(data.getId());
    }

    @Action(displayName = "禁用", contextType = ActionContextTypeEnum.SINGLE)
    public EipOpenInterface dataStatusDisable(EipOpenInterface data) {
        eipOpenInterfaceService.disable(data);
        return new EipOpenInterface().queryById(data.getId());
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<EipOpenInterface> queryPage(Pagination<EipOpenInterface> page, IWrapper<EipOpenInterface> queryWrapper) {
        return eipOpenInterfaceService.queryPage(page, queryWrapper);
    }

    @Transactional
    @Action.Advanced(invisible = "context.activeRecord.isIgnoreLogFrequency == false")
    @Action(displayName = "忽略日志记录频率限制", contextType = ActionContextTypeEnum.SINGLE, bindingType = {ViewTypeEnum.TABLE})
    public EipOpenInterface ignoreLogFrequency(EipOpenInterface data) {
        data = data.queryById();
        eipLogStrategyService.ignoreFrequency(data.getInterfaceName(), InterfaceTypeEnum.OPEN);
        data.setIsIgnoreLogFrequency(true);
        return data;
    }

    @Transactional
    @Action.Advanced(invisible = "context.activeRecord.isIgnoreLogFrequency == true")
    @Action(displayName = "取消忽略日志记录频率限制", contextType = ActionContextTypeEnum.SINGLE, bindingType = {ViewTypeEnum.TABLE})
    public EipOpenInterface cancelIgnoreLogFrequency(EipOpenInterface data) {
        data = data.queryById();
        eipLogStrategyService.cancelIgnoreFrequency(data.getInterfaceName(), InterfaceTypeEnum.OPEN);
        data.setIsIgnoreLogFrequency(false);
        return data;
    }
}
