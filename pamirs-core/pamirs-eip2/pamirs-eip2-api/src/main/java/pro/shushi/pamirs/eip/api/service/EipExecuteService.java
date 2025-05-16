package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.entity.EipResult;

/**
 * eip 执行服务
 *
 * @param <T> 上下文承载对象类型
 * @author Adamancy Zhang at 10:56 on 2020-11-30
 */
public interface EipExecuteService<T> {

    EipResult<T> callByInterfaceNameNoBody(String interfaceName);

    EipResult<T> callByInterfaceNameAndBody(String interfaceName, Object body);

    EipResult<T> callByInterfaceName(String interfaceName, T executorContext, Object body);

    EipResult<T> callByInterfaceNoBody(IEipIntegrationInterface<T> eipInterface);

    EipResult<T> callByInterfaceAndBody(IEipIntegrationInterface<T> eipInterface, Object body);

    EipResult<T> callByInterface(IEipIntegrationInterface<T> eipInterface, T executorContext, Object body);

}
