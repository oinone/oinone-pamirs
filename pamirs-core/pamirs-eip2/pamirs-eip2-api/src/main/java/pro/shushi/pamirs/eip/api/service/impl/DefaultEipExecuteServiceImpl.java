package pro.shushi.pamirs.eip.api.service.impl;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.service.EipExecuteService;

/**
 * @author Adamancy Zhang
 * @date 2020-11-30 11:00
 */
@Service
public class DefaultEipExecuteServiceImpl extends AbstractEipExecuteService<SuperMap> implements EipExecuteService<SuperMap> {

    @Override
    public EipResult<SuperMap> callByInterfaceNameNoBody(String interfaceName) {
        return super.callByInterfaceName(interfaceName, null, null);
    }

    @Override
    public EipResult<SuperMap> callByInterfaceNameAndBody(String interfaceName, Object body) {
        return super.callByInterfaceName(interfaceName, null, body);
    }

    @Override
    public EipResult<SuperMap> callByInterfaceName(String interfaceName, SuperMap executorContext, Object body) {
        return super.callByInterfaceName(interfaceName, executorContext, body);
    }

    @Override
    public EipResult<SuperMap> callByInterfaceNoBody(IEipIntegrationInterface<SuperMap> eipInterface) {
        return super.callByInterface(eipInterface, null, null);
    }

    @Override
    public EipResult<SuperMap> callByInterfaceAndBody(IEipIntegrationInterface<SuperMap> eipInterface, Object body) {
        return super.callByInterface(eipInterface, null, body);
    }

    @Override
    public EipResult<SuperMap> callByInterface(IEipIntegrationInterface<SuperMap> eipInterface, SuperMap executorContext, Object body) {
        return super.callByInterface(eipInterface, executorContext, body);
    }
}
