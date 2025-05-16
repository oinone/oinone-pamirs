package pro.shushi.pamirs.eip.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.EipProtocolTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.tmodel.EipIntegrationInterfaceEdit;
import pro.shushi.pamirs.eip.api.service.model.EipIntegrationInterfaceService;
import pro.shushi.pamirs.eip.view.manager.EipIntegrationInterfaceEditManager;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

@Component
@Model.model(EipIntegrationInterfaceEdit.MODEL_MODEL)
public class EipIntegrationInterfaceEditAction {

    @Autowired
    private EipIntegrationInterfaceEditManager eipIntegrationInterfaceEditManager;

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public EipIntegrationInterfaceEdit construct(EipIntegrationInterface data) {
        Long id = data.getId();
        EipIntegrationInterfaceEdit interfaceEdit = null;
        if (id != null) {
            EipIntegrationInterface eipIntegrationInterface = CommonApiFactory.getApi(EipIntegrationInterfaceService.class).queryById(id);
            interfaceEdit = eipIntegrationInterfaceEditManager.construct(eipIntegrationInterface);
            interfaceEdit.setId(id);
        } else {
            interfaceEdit = new EipIntegrationInterfaceEdit();
        }
        interfaceEdit.setProtocolTypeEnum(EipProtocolTypeEnum.HTTP);
        return interfaceEdit;
    }

    @Action(displayName = "创建接口")
    public EipIntegrationInterfaceEdit create(EipIntegrationInterfaceEdit frontCreate) {
        frontCreate.setDataStatus(DataStatusEnum.NOT_ENABLED);
        boolean byInterfaceEdit = eipIntegrationInterfaceEditManager.createByInterfaceEdit(frontCreate);
        return frontCreate;
    }

    @Action(displayName = "修改接口")
    public EipIntegrationInterfaceEdit update(EipIntegrationInterfaceEdit frontCreate) {
        boolean byInterfaceEdit = eipIntegrationInterfaceEditManager.updateByInterfaceEdit(frontCreate);
        return frontCreate;
    }

    @Action(displayName = "复制接口")
    public EipIntegrationInterfaceEdit copy(EipIntegrationInterfaceEdit frontCreate) {
        frontCreate.setDataStatus(DataStatusEnum.NOT_ENABLED);
        frontCreate.setId(null);
        boolean byInterfaceEdit = eipIntegrationInterfaceEditManager.createByInterfaceEdit(frontCreate);
        return frontCreate;
    }

}
