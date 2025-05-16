package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipLogStrategy;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;
import java.util.Set;

/**
 * @author yeshenyue on 2024/9/27 11:53.
 */
@Fun(EipRemoteExecuteService.FUN_NAMESPACE)
public interface EipLogStrategyService {

    String FUN_NAMESPACE = "eip.EipLogStrategyService";

    /**
     * 增加忽略日志频率限制接口
     */
    @Function
    EipLogStrategy ignoreFrequency(String interfaceName, InterfaceTypeEnum interfaceType);

    /**
     * 取消忽略日志频率限制接口
     */
    @Function
    EipLogStrategy cancelIgnoreFrequency(String interfaceName, InterfaceTypeEnum interfaceType);

    /**
     * 查询忽略日志频率限制的接口技术名称
     */
    @Function
    Set<String> queryIgnoreFrequencyList(List<String> interfaceNameList, InterfaceTypeEnum interfaceType);

    /**
     * 查询是否忽略日志频率限制
     */
    @Function
    Boolean queryIgnoreFrequency(String interfaceName, InterfaceTypeEnum interfaceType);
}
