package pro.shushi.pamirs.eip.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.AbstractEipApi;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.eip.api.service.EipAsyncService;
import pro.shushi.pamirs.eip.api.service.EipDistributionSupport;
import pro.shushi.pamirs.eip.api.service.EipService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.trigger.annotation.XAsync;

/**
 * @author Adamancy Zhang at 16:49 on 2025-08-14
 */
@Component
@Fun(EipAsyncService.FUN_NAMESPACE)
public class EipAsyncServiceImpl implements EipAsyncService {

    @Autowired
    private EipService eipService;

    @Autowired(required = false)
    private EipDistributionSupport distributionSupport;

    @XAsync(displayName = "注册集成接口")
    @Function
    @Override
    public void registerInterface(String interfaceName, Boolean isIgnoreLogFrequency) {
        EipIntegrationInterface api = fetchApi(EipIntegrationInterface.MODEL_MODEL, interfaceName, true);
        if (api == null) {
            throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).appendMsg("interfaceName: " + interfaceName).errThrow();
        }
        api.setDataStatus(DataStatusEnum.ENABLED);
        api.setIsIgnoreLogFrequency(isIgnoreLogFrequency);
        if (distributionSupport == null) {
            eipService.registerInterface(api);
        } else {
            distributionSupport.refreshInterface(api);
        }
    }

    @XAsync(displayName = "注销集成接口")
    @Function
    @Override
    public void cancellationInterface(String interfaceName) {
        EipIntegrationInterface api = fetchApi(EipIntegrationInterface.MODEL_MODEL, interfaceName, false);
        if (api == null) {
            return;
        }
        api.setDataStatus(DataStatusEnum.DISABLED);
        if (distributionSupport == null) {
            eipService.cancellationInterface(api);
        } else {
            distributionSupport.refreshInterface(api);
        }
    }

    @XAsync(displayName = "注册路由定义")
    @Function
    @Override
    public void registerRouteDefinition(String interfaceName, Boolean isIgnoreLogFrequency) {
        EipRouteDefinition api = fetchApi(EipRouteDefinition.MODEL_MODEL, interfaceName, true);
        if (api == null) {
            throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).appendMsg("interfaceName: " + interfaceName).errThrow();
        }
        api.setDataStatus(DataStatusEnum.ENABLED);
        api.setIsIgnoreLogFrequency(isIgnoreLogFrequency);
        if (distributionSupport == null) {
            eipService.registerRouteDefinition(api);
        } else {
            distributionSupport.refreshRouteDefinition(api);
        }
    }

    @XAsync(displayName = "注销路由定义")
    @Function
    @Override
    public void cancellationRouteDefinition(String interfaceName) {
        EipRouteDefinition api = fetchApi(EipRouteDefinition.MODEL_MODEL, interfaceName, false);
        if (api == null) {
            return;
        }
        api.setDataStatus(DataStatusEnum.DISABLED);
        if (distributionSupport == null) {
            eipService.cancellationRouteDefinition(api);
        } else {
            distributionSupport.refreshRouteDefinition(api);
        }
    }

    @XAsync(displayName = "注册开放接口")
    @Function
    @Override
    public void registerOpenInterface(String interfaceName, Boolean isIgnoreLogFrequency) {
        EipOpenInterface api = fetchApi(EipOpenInterface.MODEL_MODEL, interfaceName, true);
        if (api == null) {
            throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).appendMsg("interfaceName: " + interfaceName).errThrow();
        }
        api.setDataStatus(DataStatusEnum.ENABLED);
        api.setIsIgnoreLogFrequency(isIgnoreLogFrequency);
        if (distributionSupport == null) {
            eipService.registerOpenInterface(api);
        } else {
            distributionSupport.refreshOpenInterface(api);
        }
    }

    @XAsync(displayName = "注销开放接口")
    @Function
    @Override
    public void cancellationOpenInterface(String interfaceName) {
        EipOpenInterface api = fetchApi(EipOpenInterface.MODEL_MODEL, interfaceName, false);
        if (api == null) {
            return;
        }
        api.setDataStatus(DataStatusEnum.DISABLED);
        if (distributionSupport == null) {
            eipService.cancellationOpenInterface(api);
        } else {
            distributionSupport.refreshOpenInterface(api);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractEipApi> T fetchApi(String model, String interfaceName, boolean nullable) {
        T api = Models.origin().queryOneByWrapper(Pops.<T>lambdaQuery()
                .from(model)
                .eq(AbstractEipApi::getInterfaceName, interfaceName));
        if (api == null) {
            if (nullable) {
                return null;
            }
            switch (model) {
                case EipIntegrationInterface.MODEL_MODEL:
                    api = (T) new EipIntegrationInterface().setInterfaceName(interfaceName);
                    break;
                case EipRouteDefinition.MODEL_MODEL:
                    api = (T) new EipRouteDefinition().setInterfaceName(interfaceName);
                    break;
                case EipOpenInterface.MODEL_MODEL:
                    api = (T) new EipOpenInterface().setInterfaceName(interfaceName);
                    break;
            }
        }
        return api;
    }
}
