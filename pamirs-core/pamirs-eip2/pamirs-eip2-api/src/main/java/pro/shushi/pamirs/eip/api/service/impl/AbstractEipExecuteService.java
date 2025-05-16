package pro.shushi.pamirs.eip.api.service.impl;

import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.service.EipExecuteService;

/**
 * @author Adamancy Zhang
 * @date 2020-11-30 11:05
 */
public abstract class AbstractEipExecuteService<T> implements EipExecuteService<T> {

    @Override
    public EipResult<T> callByInterfaceNameNoBody(String interfaceName) {
        return EipInterfaceContext.call(interfaceName, null, null);
    }

    @Override
    public EipResult<T> callByInterfaceNameAndBody(String interfaceName, Object body) {
        return EipInterfaceContext.call(interfaceName, null, body);
    }

    @Override
    public EipResult<T> callByInterfaceName(String interfaceName, T executorContext, Object body) {
        return EipInterfaceContext.call(interfaceName, executorContext, body);
    }

    @Override
    public EipResult<T> callByInterfaceNoBody(IEipIntegrationInterface<T> eipInterface) {
        return EipInterfaceContext.call(eipInterface, null, null);
    }

    @Override
    public EipResult<T> callByInterfaceAndBody(IEipIntegrationInterface<T> eipInterface, Object body) {
        return EipInterfaceContext.call(eipInterface, null, body);
    }

    @Override
    public EipResult<T> callByInterface(IEipIntegrationInterface<T> eipInterface, T executorContext, Object body) {
        return EipInterfaceContext.call(eipInterface, executorContext, body);
    }
}
