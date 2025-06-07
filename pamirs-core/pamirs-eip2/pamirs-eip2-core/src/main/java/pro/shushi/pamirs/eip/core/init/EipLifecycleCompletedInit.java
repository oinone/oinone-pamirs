package pro.shushi.pamirs.eip.core.init;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.LifecycleCompletedInit;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.EipModule;
import pro.shushi.pamirs.eip.api.config.PamirsEipOpenApiProperties;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.AbstractEipApi;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.eip.api.service.EipDistributionSupport;
import pro.shushi.pamirs.eip.api.service.EipInterfaceService;
import pro.shushi.pamirs.eip.api.service.EipLogStrategyService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Adamancy Zhang
 * @date 2021-01-06 14:15
 */
@Slf4j
@Component
@Order(0)
public class EipLifecycleCompletedInit implements LifecycleCompletedInit {

    @Autowired(required = false)
    private PamirsEipOpenApiProperties openApiConfiguration;

    @Autowired
    private EipInterfaceService interfaceService;

    @Autowired(required = false)
    private EipDistributionSupport distributorSupport;

    @Autowired
    private EipLogStrategyService eipLogStrategyService;

    @Override
    public void process(AppLifecycleCommand command, List<ModuleDefinition> installModules, List<ModuleDefinition> upgradeModules, List<ModuleDefinition> reloadModules) {
        InitializationUtil.lifecycleCompletedInit(installModules, upgradeModules, reloadModules, (lifecycle, module) -> {
            init();
            if (distributorSupport != null) {
                try {
                    distributorSupport.start();
                    log.info("eip distribution supported.");
                } catch (Exception e) {
                    log.error("eip distribution unsupported.", e);
                }
            }
        }, EipModule.MODULE_MODULE);
    }

    private void init() {
        try {
            EipCamelContext camelContext = EipCamelContext.getContext();
            EipInterfaceContext.routeInitialization(camelContext);
            camelContext.start();
        } catch (Exception e) {
            log.error("eip route definition initialization error.", e);
            return;
        }
        addIntegrationInterfaceFromDBToCamel();
        addRouteDefinitionFromDBToCamel();
        if (openApiConfiguration != null) {
            if (openApiConfiguration.getRoute() != null) {
                addOpenInterfaceFromDBToCamel();
            }
        }
    }

    public void addIntegrationInterfaceFromDBToCamel() {
        List<EipIntegrationInterface> integrationInterfaceList = new EipIntegrationInterface()
                .setDataStatus(DataStatusEnum.ENABLED)
                .queryList();
        Set<String> ignoreFrequencyInterfaceNames = fetchIgnoreLogFrequencyInterfaceNames(integrationInterfaceList, InterfaceTypeEnum.INTEGRATION);
        for (EipIntegrationInterface integrationInterface : integrationInterfaceList) {
            integrationInterface.setIsIgnoreLogFrequency(ignoreFrequencyInterfaceNames.contains(integrationInterface.getInterfaceName()));
            interfaceService.registerInterface(integrationInterface);
        }
    }

    public void addRouteDefinitionFromDBToCamel() {
        List<EipRouteDefinition> routeDefinitionList = new EipRouteDefinition()
                .setDataStatus(DataStatusEnum.ENABLED)
                .queryList();
        Set<String> ignoreFrequencyInterfaceNames = fetchIgnoreLogFrequencyInterfaceNames(routeDefinitionList, InterfaceTypeEnum.ROUTE);
        for (EipRouteDefinition routeDefinition : routeDefinitionList) {
            routeDefinition.setIsIgnoreLogFrequency(ignoreFrequencyInterfaceNames.contains(routeDefinition.getInterfaceName()));
            interfaceService.registerRouteDefinition(routeDefinition);
        }
    }

    public void addOpenInterfaceFromDBToCamel() {
        List<EipOpenInterface> openInterfaceList = new EipOpenInterface()
                .setDataStatus(DataStatusEnum.ENABLED)
                .queryList();
        Set<String> ignoreFrequencyInterfaceNames = fetchIgnoreLogFrequencyInterfaceNames(openInterfaceList, InterfaceTypeEnum.OPEN);
        for (EipOpenInterface openInterface : openInterfaceList) {
            openInterface.setIsIgnoreLogFrequency(ignoreFrequencyInterfaceNames.contains(openInterface.getInterfaceName()));
            interfaceService.registerOpenInterface(openInterface);
        }
    }

    /**
     * 获取忽略日志频率限制的接口技术名称
     */
    private <T extends AbstractEipApi> Set<String> fetchIgnoreLogFrequencyInterfaceNames(List<T> interfaceList, InterfaceTypeEnum interfaceTypeEnum) {
        List<String> interfaceNames = interfaceList.stream()
                .map(T::getInterfaceName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        return eipLogStrategyService.queryIgnoreFrequencyList(interfaceNames, interfaceTypeEnum);
    }
}
