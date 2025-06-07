package pro.shushi.pamirs.eip.view.manager.edit.preparer;


import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipLib;
import pro.shushi.pamirs.eip.api.service.model.EipIntegrationInterfaceService;
import pro.shushi.pamirs.eip.api.service.model.EipLibService;
import pro.shushi.pamirs.eip.api.tmodel.EipIntegrationInterfaceEdit;
import pro.shushi.pamirs.eip.view.manager.edit.EipInterfaceEditPrepareChain;
import pro.shushi.pamirs.eip.view.manager.edit.EipInterfaceEditPreparer;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;
import java.util.Optional;


@Component
@Order(1)
public class CommonPreparer<R> implements EipInterfaceEditPreparer<R> {

    @Autowired
    private EipIntegrationInterfaceService eipIntegrationInterfaceService;
    @Autowired
    private EipLibService eipLibService;

    @Override
    public R prepare(EipIntegrationInterfaceEdit interfaceEdit, EipInterfaceEditPrepareChain<R> prepareChain) {
        EipIntegrationInterface eipIntegrationInterface = prepareChain.getEipIntegrationInterface();
        String interfaceName = interfaceEdit.getInterfaceName();
        if (null == interfaceEdit.getId()) {
            LambdaQueryWrapper<EipIntegrationInterface> wrapper = Pops.<EipIntegrationInterface>lambdaQuery()
                    .from(EipIntegrationInterface.MODEL_MODEL)
                    .eq(EipIntegrationInterface::getInterfaceName, interfaceName);
            List<EipIntegrationInterface> interfaceList = eipIntegrationInterfaceService.queryListByWrapper(wrapper);
            if (CollectionUtils.isNotEmpty(interfaceList)) {
                throw PamirsException.construct(EipExpEnumerate.INTERFACE_NAME_EXIST).errThrow();
            }
        } else {
            eipIntegrationInterface.setId(interfaceEdit.getId());
        }
        eipIntegrationInterface.setLibCode(Optional.ofNullable(interfaceEdit.getLib()).map(EipLib::getCode).orElse(null));
        eipIntegrationInterface.setInterfaceName(interfaceEdit.getInterfaceName());
        eipIntegrationInterface.setName(interfaceEdit.getName());
        eipIntegrationInterface.setIsEnabledLog(interfaceEdit.getIsEnabledLog());
        eipIntegrationInterface.setUri(interfaceEdit.getUri());
        eipIntegrationInterface.setProtocolTypeEnum(interfaceEdit.getProtocolTypeEnum());
        eipIntegrationInterface.setDataStatus(interfaceEdit.getDataStatus());
        eipIntegrationInterface.setModule(Optional.ofNullable(interfaceEdit.getModuleDefinition()).map(ModuleDefinition::getModule).orElse(null));
        return prepareChain.prepare(interfaceEdit, prepareChain);
    }

    @Override
    public R construct(EipIntegrationInterface integrationInterface, EipInterfaceEditPrepareChain<R> prepareChain) {
        EipIntegrationInterfaceEdit edit = prepareChain.getEipIntegrationInterfaceEdit();
        String libCode = integrationInterface.getLibCode();
        Optional.ofNullable(libCode).ifPresent(_code -> {
            EipLib eipLib = eipLibService.queryByCode(libCode);
            edit.setLib(eipLib);
        });
        Optional.ofNullable(integrationInterface.getModule()).ifPresent(_module -> {
            ModuleDefinition moduleDefinition = new ModuleDefinition().setModule(_module).queryOne();
            edit.setModuleDefinition(moduleDefinition);
        });
        edit.setInterfaceName(integrationInterface.getInterfaceName());
        edit.setName(integrationInterface.getName());
        edit.setIsEnabledLog(integrationInterface.getIsEnabledLog());
        edit.setUri(integrationInterface.getUri());
        edit.setProtocolTypeEnum(integrationInterface.getProtocolTypeEnum());
        edit.setDataStatus(integrationInterface.getDataStatus());
        return prepareChain.construct(integrationInterface, prepareChain);
    }
}
