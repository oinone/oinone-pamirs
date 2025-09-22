package pro.shushi.pamirs.eip.core.init;

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
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.eip.api.service.EipInterfaceService;
import pro.shushi.pamirs.eip.api.service.distribution.EipDistributionService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2021-01-06 14:15
 */
@Slf4j
@Order(0)
@Component
public class EipLifecycleCompletedInit implements LifecycleCompletedInit {

    @Autowired(required = false)
    private PamirsEipOpenApiProperties openApiConfiguration;

    @Autowired
    private EipInterfaceService interfaceService;

    @Override
    public void process(AppLifecycleCommand command, List<ModuleDefinition> installModules, List<ModuleDefinition> upgradeModules, List<ModuleDefinition> reloadModules) {
        InitializationUtil.lifecycleCompletedInit(installModules, upgradeModules, reloadModules, (lifecycle, module) -> {
            init();
            List<EipDistributionService> services = BeanDefinitionUtils.getBeansOfTypeByOrdered(EipDistributionService.class);
            for (EipDistributionService distributionService : services) {
                try {
                    distributionService.start();
                    log.info("eip distribution supported. {}", distributionService.getClass().getName());
                } catch (Exception e) {
                    log.error("eip distribution unsupported. {}", distributionService.getClass().getName(), e);
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
        for (EipIntegrationInterface integrationInterface : integrationInterfaceList) {
            interfaceService.registerInterface(integrationInterface);
        }
    }

    public void addRouteDefinitionFromDBToCamel() {
        List<EipRouteDefinition> routeDefinitionList = new EipRouteDefinition()
                .setDataStatus(DataStatusEnum.ENABLED)
                .queryList();
        for (EipRouteDefinition routeDefinition : routeDefinitionList) {
            interfaceService.registerRouteDefinition(routeDefinition);
        }
    }

    public void addOpenInterfaceFromDBToCamel() {
        List<EipOpenInterface> openInterfaceList = new EipOpenInterface()
                .setDataStatus(DataStatusEnum.ENABLED)
                .queryList();
        for (EipOpenInterface openInterface : openInterfaceList) {
            interfaceService.registerOpenInterface(openInterface);
        }
    }
}
