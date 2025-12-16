package pro.shushi.pamirs.eip.api.strategy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.strategy.context.EipLogStrategyContext;
import pro.shushi.pamirs.eip.api.strategy.entity.EipLogStrategyEntity;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogStrategyAsyncService;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogStrategyDistributionSupport;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.trigger.annotation.XAsync;

/**
 * EIP日志策略异步服务默认实现
 *
 * @author Adamancy Zhang at 19:30 on 2025-10-31
 */
@Service
@Fun(EipLogStrategyAsyncService.FUN_NAMESPACE)
public class EipLogStrategyAsyncServiceImpl implements EipLogStrategyAsyncService {

    @Autowired(required = false)
    private EipLogStrategyDistributionSupport eipLogStrategyDistributionSupport;

    @XAsync(displayName = "刷新日志策略")
    @Function
    @Override
    public void refreshLogStrategy(EipLogStrategyEntity logStrategy) {
        if (eipLogStrategyDistributionSupport != null && eipLogStrategyDistributionSupport.isStart()) {
            eipLogStrategyDistributionSupport.refreshLogStrategy(logStrategy);
        } else {
            EipLogStrategyContext.put(logStrategy.getInterfaceType(), logStrategy.getInterfaceName(), logStrategy);
        }
    }
}
