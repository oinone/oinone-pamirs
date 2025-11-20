package pro.shushi.pamirs.eip.api.strategy.service;

import pro.shushi.pamirs.eip.api.strategy.entity.EipLogStrategyEntity;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * EIP日志策略异步服务
 *
 * @author Adamancy Zhang at 19:28 on 2025-10-31
 */
@Fun(EipLogStrategyAsyncService.FUN_NAMESPACE)
public interface EipLogStrategyAsyncService {

    String FUN_NAMESPACE = "eip.EipLogStrategyAsyncService";

    @Function
    void refreshLogStrategy(EipLogStrategyEntity logStrategy);
}
