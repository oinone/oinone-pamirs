package pro.shushi.pamirs.eip.api.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipLogStrategy;
import pro.shushi.pamirs.eip.api.service.EipLogStrategyService;
import pro.shushi.pamirs.eip.api.service.EipRemoteExecuteService;
import pro.shushi.pamirs.eip.api.util.EipInitializationUtil;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yeshenyue on 2024/9/27 12:00.
 */
@Slf4j
@Service
@Fun(EipRemoteExecuteService.FUN_NAMESPACE)
public class EipLogStrategyServiceImpl implements EipLogStrategyService {

    @Override
    @Function
    public EipLogStrategy ignoreFrequency(String interfaceName, InterfaceTypeEnum interfaceType) {
        EipLogStrategy strategy = build(interfaceName, interfaceType);
        strategy.setIsIgnoreFrequency(true);
        strategy.createOrUpdate();
        return strategy;
    }

    @Override
    @Function
    public EipLogStrategy cancelIgnoreFrequency(String interfaceName, InterfaceTypeEnum interfaceType) {
        EipLogStrategy strategy = build(interfaceName, interfaceType);
        strategy.setIsIgnoreFrequency(false);
        strategy.createOrUpdate();
        return strategy;
    }

    @Override
    @Function
    public Set<String> queryIgnoreFrequencyList(List<String> interfaceNameList, InterfaceTypeEnum interfaceType) {
        if (CollectionUtils.isEmpty(interfaceNameList) || interfaceType == null) {
            return Collections.emptySet();
        }
        List<EipLogStrategy> eipLogStrategyList = Models.data().queryListByWrapper(Pops.<EipLogStrategy>lambdaQuery()
                .from(EipLogStrategy.MODEL_MODEL)
                .eq(EipLogStrategy::getInterfaceType, interfaceType)
                .eq(EipLogStrategy::getIsIgnoreFrequency, true)
                .in(EipLogStrategy::getInterfaceName, interfaceNameList)
                .select(EipLogStrategy::getId, EipLogStrategy::getInterfaceName)
        );
        return eipLogStrategyList.stream().map(EipLogStrategy::getInterfaceName).collect(Collectors.toSet());
    }

    @Override
    @Function
    public Boolean queryIgnoreFrequency(String interfaceName, InterfaceTypeEnum interfaceType) {
        EipLogStrategy strategy = build(interfaceName, interfaceType);
        strategy.setIsIgnoreFrequency(true);
        strategy = strategy.queryOne();
        return strategy != null;
    }

    @Override
    @Function
    public Set<String> queryEnableIgnoreLogConfig(List<String> interfaceNameList) {
        if (CollectionUtils.isEmpty(interfaceNameList)) {
            return Collections.emptySet();
        }
        IWrapper<EipLogStrategy> queryWrapper = Pops.<EipLogStrategy>lambdaQuery().from(EipLogStrategy.MODEL_MODEL)
                .eq(EipLogStrategy::getIsIgnoreFrequency, true)
                .isNotNull(EipLogStrategy::getInterfaceType)
                .in(EipLogStrategy::getInterfaceName, interfaceNameList);
        List<EipLogStrategy> eipLogStrategyList = Models.data().queryListByWrapper(queryWrapper);

        return fetchInterfaceNames(eipLogStrategyList);
    }

    private static Set<String> fetchInterfaceNames(List<EipLogStrategy> eipLogStrategyList) {
        Set<String> result = new HashSet<>();
        for (EipLogStrategy eipLogStrategy : eipLogStrategyList) {
            switch (eipLogStrategy.getInterfaceType()) {
                case OPEN:
                    result.add(EipInitializationUtil.generatorOpenApiRouteId(eipLogStrategy.getInterfaceName()));
                    break;
                case ROUTE:
                case INTEGRATION:
                    result.add(EipInitializationUtil.generatorIntegrationInterfaceRouteId(eipLogStrategy.getInterfaceName()));
                    break;
                default:
                    throw PamirsException.construct(EipExpEnumerate.EIP_UNSUPPORTED_INTERFACE_TYPE).errThrow();
            }
        }
        return result;
    }

    private EipLogStrategy build(String interfaceName, InterfaceTypeEnum interfaceType) {
        EipLogStrategy strategy = new EipLogStrategy();
        strategy.setInterfaceName(interfaceName);
        strategy.setInterfaceType(interfaceType);
        return strategy;
    }
}
