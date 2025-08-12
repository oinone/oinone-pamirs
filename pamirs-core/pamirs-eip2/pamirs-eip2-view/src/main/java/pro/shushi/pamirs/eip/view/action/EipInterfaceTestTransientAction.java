package pro.shushi.pamirs.eip.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipInterfaceTestTransient;
import pro.shushi.pamirs.eip.api.service.EipInterfaceTestService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

@Component
@Model.model(EipInterfaceTestTransient.MODEL_MODEL)
public class EipInterfaceTestTransientAction {

    @Autowired
    private EipInterfaceTestService eipInterfaceTestService;

    @Function(openLevel = FunctionOpenEnum.API, summary = "集成接口测试构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public EipInterfaceTestTransient construct(EipInterfaceTestTransient data, EipIntegrationInterface integrationInterface) {
        return eipInterfaceTestService.construct(data, integrationInterface);
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "集成接口下拉触发")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public EipInterfaceTestTransient constructMirror(EipInterfaceTestTransient data) {
        return eipInterfaceTestService.construct(data, data.getIntegrationInterface());
    }

    @Action(displayName = "模拟参数转换", contextType = ActionContextTypeEnum.CONTEXT_FREE, bindingType = {ViewTypeEnum.FORM})
    public EipInterfaceTestTransient mockParamConverter(EipInterfaceTestTransient data) {
        return eipInterfaceTestService.mockParamConverter(data);
    }

    @Action(displayName = "测试", contextType = ActionContextTypeEnum.CONTEXT_FREE, bindingType = {ViewTypeEnum.FORM})
    public EipInterfaceTestTransient test(EipInterfaceTestTransient data) {
        return eipInterfaceTestService.test(data);
    }
}
