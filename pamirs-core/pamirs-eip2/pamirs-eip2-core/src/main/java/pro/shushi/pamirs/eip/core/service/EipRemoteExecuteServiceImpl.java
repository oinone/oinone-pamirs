package pro.shushi.pamirs.eip.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.service.EipExecuteService;
import pro.shushi.pamirs.eip.api.service.EipRemoteExecuteService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.x.XService;

/**
 * {@link EipRemoteExecuteService}实现
 *
 * @author Adamancy Zhang at 11:31 on 2022-04-01
 */
@Service
@XService
@Fun(EipRemoteExecuteService.FUN_NAMESPACE)
public class EipRemoteExecuteServiceImpl implements EipRemoteExecuteService {

    @Autowired
    private EipExecuteService<SuperMap> executeService;

    @Function
    @Override
    public EipResult<SuperMap> callByInterfaceNameNoBody(String interfaceName) {
        return executeService.callByInterfaceNameNoBody(interfaceName);
    }

    @Function
    @Override
    public EipResult<SuperMap> callByInterfaceNameAndBody(String interfaceName, Object body) {
        return executeService.callByInterfaceNameAndBody(interfaceName, body);
    }

    @Function
    @Override
    public EipResult<SuperMap> callByInterfaceName(String interfaceName, SuperMap executorContext, Object body) {
        return executeService.callByInterfaceName(interfaceName, executorContext, body);
    }
}
