package pro.shushi.pamirs.eip.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.eip.api.service.EipAsyncService;
import pro.shushi.pamirs.eip.api.service.EipDistributionSupport;
import pro.shushi.pamirs.eip.api.service.EipInterfaceService;
import pro.shushi.pamirs.eip.api.service.EipService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;

@Slf4j
@Service
public class EipServiceImpl implements EipService {

    @Autowired
    private EipInterfaceService interfaceService;

    @Autowired(required = false)
    private EipDistributionSupport distributionSupport;

    @Autowired
    private EipAsyncService eipAsyncService;

    @SuppressWarnings("rawtypes")
    @Override
    public void registerApi(IEipApi eipApi) {
        if (eipApi instanceof IEipOpenInterface) {
            if (eipApi instanceof EipOpenInterface) {
                registerOpenInterface((EipOpenInterface) eipApi);
            } else {
                Result<String> result = interfaceService.registerOpenInterface((IEipOpenInterface) eipApi);
                if (!result.isSuccess()) {
                    throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(result.getData()).errThrow();
                }
            }
        } else if (eipApi instanceof IEipIntegrationInterface) {
            if (eipApi instanceof EipIntegrationInterface) {
                registerInterface((EipIntegrationInterface) eipApi);
            } else {
                Result<String> result = interfaceService.registerInterface((IEipIntegrationInterface) eipApi);
                if (!result.isSuccess()) {
                    throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(result.getData()).errThrow();
                }
            }
        } else if (eipApi instanceof EipRouteDefinition) {
            registerRouteDefinition((EipRouteDefinition) eipApi);
        } else {
            throw new UnsupportedOperationException("不支持的接口类型");
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void cancellationApi(IEipApi eipApi) {
        if (eipApi instanceof IEipOpenInterface) {
            if (eipApi instanceof EipOpenInterface) {
                cancellationOpenInterface((EipOpenInterface) eipApi);
            } else {
                Result<String> result = interfaceService.cancellationOpenInterface((IEipOpenInterface) eipApi);
                if (!result.isSuccess()) {
                    throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(result.getData()).errThrow();
                }
            }
        } else if (eipApi instanceof IEipIntegrationInterface) {
            if (eipApi instanceof EipIntegrationInterface) {
                cancellationInterface((EipIntegrationInterface) eipApi);
            } else {
                Result<String> result = interfaceService.cancellationInterface((IEipIntegrationInterface) eipApi);
                if (!result.isSuccess()) {
                    throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(result.getData()).errThrow();
                }
            }
        } else if (eipApi instanceof EipRouteDefinition) {
            cancellationRouteDefinition((EipRouteDefinition) eipApi);
        } else {
            throw new UnsupportedOperationException("不支持的接口类型");
        }
    }

    @Override
    public void registerInterface(EipIntegrationInterface eipInterface) {
        Result<String> result;
        if (distributionSupport != null && eipInterface.getIsDBManaged()) {
            eipAsyncService.registerInterface(eipInterface.getInterfaceName(), eipInterface.getIsIgnoreLogFrequency());
            result = new Result<>();
        } else {
            result = interfaceService.registerInterface(eipInterface);
        }
        if (!result.isSuccess()) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(result.getData()).errThrow();
        }
    }

    @Override
    public void cancellationInterface(EipIntegrationInterface eipInterface) {
        Result<String> result;
        if (distributionSupport != null && eipInterface.getIsDBManaged()) {
            eipAsyncService.cancellationInterface(eipInterface.getInterfaceName());
            result = new Result<>();
        } else {
            result = interfaceService.cancellationInterface(eipInterface);
        }
        if (!result.isSuccess()) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(result.getData()).errThrow();
        }
    }

    @Override
    public void registerRouteDefinition(EipRouteDefinition eipInterface) {
        Result<String> result;
        if (distributionSupport != null && eipInterface.getIsDBManaged()) {
            eipAsyncService.registerRouteDefinition(eipInterface.getInterfaceName(), eipInterface.getIsIgnoreLogFrequency());
            result = new Result<>();
        } else {
            result = interfaceService.registerRouteDefinition(eipInterface);
        }
        if (!result.isSuccess()) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(result.getData()).errThrow();
        }
    }

    @Override
    public void cancellationRouteDefinition(EipRouteDefinition eipInterface) {
        Result<String> result;
        if (distributionSupport != null && eipInterface.getIsDBManaged()) {
            eipAsyncService.cancellationRouteDefinition(eipInterface.getInterfaceName());
            result = new Result<>();
        } else {
            result = interfaceService.cancellationRouteDefinition(eipInterface);
        }
        if (!result.isSuccess()) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(result.getData()).errThrow();
        }
    }

    @Override
    public void registerOpenInterface(EipOpenInterface eipInterface) {
        Result<String> result;
        if (distributionSupport != null && eipInterface.getIsDBManaged()) {
            eipAsyncService.registerOpenInterface(eipInterface.getInterfaceName(), eipInterface.getIsIgnoreLogFrequency());
            result = new Result<>();
        } else {
            result = interfaceService.registerOpenInterface(eipInterface);
        }
        if (!result.isSuccess()) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(result.getData()).errThrow();
        }
    }

    @Override
    public void cancellationOpenInterface(EipOpenInterface eipInterface) {
        Result<String> result;
        if (distributionSupport != null && eipInterface.getIsDBManaged()) {
            eipAsyncService.cancellationOpenInterface(eipInterface.getInterfaceName());
            result = new Result<>();
        } else {
            result = interfaceService.cancellationOpenInterface(eipInterface);
        }
        if (!result.isSuccess()) {
            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(result.getData()).errThrow();
        }
    }
}
