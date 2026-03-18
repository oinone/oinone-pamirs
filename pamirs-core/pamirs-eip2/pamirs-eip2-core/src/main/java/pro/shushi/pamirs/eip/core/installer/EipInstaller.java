package pro.shushi.pamirs.eip.core.installer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.eip.api.enmu.connector.ConnType;
import pro.shushi.pamirs.eip.api.installer.EipInstallBizData;
import pro.shushi.pamirs.eip.api.installer.IEipInstaller;
import pro.shushi.pamirs.eip.api.model.*;
import pro.shushi.pamirs.eip.api.model.config.EipSingletonConfig;
import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.eip.api.model.connector.EipConnectorAuth;
import pro.shushi.pamirs.eip.api.model.connector.EipConnectorResource;
import pro.shushi.pamirs.eip.api.service.EipService;
import pro.shushi.pamirs.eip.jdbc.helper.EipConnectorHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * EipInstaller
 *
 * @author yakir on 2024/06/25 16:14.
 */
@Slf4j
@Component
@Fun(IEipInstaller.FUN_NAMESPACE)
public class EipInstaller implements IEipInstaller {

    @Autowired
    private EipService eipService;

    @Function
    @Override
    public Boolean install(String data) {

        EipInstallBizData bizData = JsonUtils.parseObject(data, EipInstallBizData.class);

        List<EipConnGroup> groupList = bizData.getGroupList();
        List<EipLib> eipLibList = bizData.getEipLibList();
        List<EipApplication> eipAppList = bizData.getEipAppList();
        List<EipOpenInterface> openApiList = bizData.getOpenApiList();
        List<EipIntegrate> integrateList = bizData.getIntegrateList();
        List<EipSingletonConfig> eipConfigList = bizData.getEipConfigList();
        List<EipIntegrationInterface> iiList = bizData.getIiList();
        List<EipIntegrationFile> ifList = bizData.getIfList();
        List<EipConnector> connList = bizData.getConnList();
        List<EipConnectorResource> connResList = bizData.getConnResList();
        List<EipConnectorAuth> authList = bizData.getAuthList();

        if (!CollectionUtils.isEmpty(groupList)) {
            List<String> groupCodes = groupList.stream().map(EipConnGroup::getCode).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            List<EipConnGroup> eipConnGroupList = Collections.emptyList();
            if (!CollectionUtils.isEmpty(groupCodes)) {
                eipConnGroupList = Models.origin().queryListByWrapper(Pops.<EipConnGroup>lambdaQuery()
                        .from(EipConnGroup.MODEL_MODEL)
                        .in(EipConnGroup::getCode, groupCodes)
                );
            }
            for (EipConnGroup group : groupList) {
                try {
                    EipConnGroup oldEipConnGroup = eipConnGroupList.stream().filter(g -> g.getCode().equals(group.getCode())).findFirst().orElse(null);
                    if (oldEipConnGroup == null) {
                        group.create();
                    } else {
                        group.updateByCode();
                    }
                } catch (Exception e) {
                    log.error("Integration designer business domain import failed, {}", JsonUtils.toJSONString(group), e);
                }
            }
        }

        for (EipLib lib : Optional.ofNullable(eipLibList).orElse(Collections.emptyList())) {
            lib.unsetId();
            lib.createOrUpdate();
        }

        for (EipApplication app : Optional.ofNullable(eipAppList).orElse(Collections.emptyList())) {
            app.unsetId();
            app.createOrUpdate();
        }

        for (EipIntegrate integrate : Optional.ofNullable(integrateList).orElse(Collections.emptyList())) {
            // EipIntegrationInterface根据id关联
            integrate.createOrUpdate();
        }

        for (EipSingletonConfig sconfig : Optional.ofNullable(eipConfigList).orElse(Collections.emptyList())) {
            sconfig.unsetId();
            sconfig.createOrUpdate();
        }

        for (EipIntegrationFile eif : Optional.ofNullable(ifList).orElse(Collections.emptyList())) {
            eif.createOrUpdate();
        }

        for (EipConnectorResource resource : Optional.ofNullable(connResList).orElse(Collections.emptyList())) {
            resource.unsetId();
            resource.createOrUpdate();
        }

        for (EipConnectorAuth auth : Optional.ofNullable(authList).orElse(Collections.emptyList())) {
            auth.unsetId();
            auth.createOrUpdate();
        }

        installConnector(connList);
        installIntegrationInterfaceList(iiList);
        installOpenInterfaceList(openApiList);
        return true;
    }

    private void installIntegrationInterfaceList(List<EipIntegrationInterface> integrationInterfaceList) {
        Set<String> integrationInterfaceNames = new HashSet<>();
        for (EipIntegrationInterface integrationInterface : Optional.ofNullable(integrationInterfaceList).orElse(Collections.emptyList())) {
            // 数据流程集成接口id写死在json，id更改会出现两个相同的集成接口选项
            integrationInterface.createOrUpdate();
            integrationInterfaceNames.add(integrationInterface.getInterfaceName());
        }
        if (!integrationInterfaceNames.isEmpty()) {
            List<EipIntegrationInterface> dbIntegrationInterfaceList = DataShardingHelper.build().collectionSharding(integrationInterfaceNames,
                    sublist -> Models.origin().queryListByWrapper(Pops.<EipIntegrationInterface>lambdaQuery()
                            .from(EipIntegrationInterface.MODEL_MODEL)
                            .in(EipIntegrationInterface::getInterfaceName, sublist)));
            for (EipIntegrationInterface integrationInterface : dbIntegrationInterfaceList) {
                eipService.registerInterface(integrationInterface);
            }
        }
    }

    private void installOpenInterfaceList(List<EipOpenInterface> openInterfaceList) {
        Set<String> openInterfaceNames = new HashSet<>();
        for (EipOpenInterface openInterface : Optional.ofNullable(openInterfaceList).orElse(Collections.emptyList())) {
            openInterface.unsetId();
            openInterface.createOrUpdate();
            openInterfaceNames.add(openInterface.getInterfaceName());
        }
        if (!openInterfaceNames.isEmpty()) {
            List<EipOpenInterface> dbOpenInterfaceList = DataShardingHelper.build().collectionSharding(openInterfaceNames,
                    sublist -> Models.origin().queryListByWrapper(Pops.<EipOpenInterface>lambdaQuery()
                            .from(EipOpenInterface.MODEL_MODEL)
                            .in(EipOpenInterface::getInterfaceName, sublist)));
            for (EipOpenInterface openInterface : dbOpenInterfaceList) {
                eipService.registerOpenInterface(openInterface);
            }
        }
    }

    private void installConnector(List<EipConnector> connectorList) {
        Set<String> connectorNames = new HashSet<>();
        for (EipConnector conn : Optional.ofNullable(connectorList).orElse(Collections.emptyList())) {
            // conn.unsetId();
            // EipConnectorResource与EipConnector通过id关联
            conn.createOrUpdate();
            connectorNames.add(conn.getName());
        }
        if (!connectorNames.isEmpty()) {
            List<EipConnector> dbConnectorList = DataShardingHelper.build().collectionSharding(connectorNames,
                    sublist -> Models.origin().queryListByWrapper(Pops.<EipConnector>lambdaQuery()
                            .from(EipConnector.MODEL_MODEL)
                            .in(EipConnector::getName, sublist)));
            for (EipConnector connector : dbConnectorList) {
                if (ConnType.DB.equals(connector.getType())) {
                    EipConnectorHelper.initConnector(connector);
                }
            }
        }
    }
}
