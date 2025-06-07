package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * eip 远程执行服务
 *
 * @author Adamancy Zhang at 11:30 on 2022-04-01
 */
@Fun(EipRemoteExecuteService.FUN_NAMESPACE)
public interface EipRemoteExecuteService {

    String FUN_NAMESPACE = "eip.EipRemoteExecuteService";

    @Function
    EipResult<SuperMap> callByInterfaceNameNoBody(String interfaceName);

    @Function
    EipResult<SuperMap> callByInterfaceNameAndBody(String interfaceName, Object body);

    @Function
    EipResult<SuperMap> callByInterfaceName(String interfaceName, SuperMap executorContext, Object body);
}
