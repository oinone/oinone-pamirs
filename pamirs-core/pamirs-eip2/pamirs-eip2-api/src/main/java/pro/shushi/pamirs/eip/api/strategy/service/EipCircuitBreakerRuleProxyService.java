package pro.shushi.pamirs.eip.api.strategy.service;

import pro.shushi.pamirs.eip.api.pmodel.EipCircuitBreakerRuleProxy;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

/**
 * @author yeshenyue on 2025/4/14 17:33.
 */
@Fun(EipCircuitBreakerRuleProxyService.FUN_NAMESPACE)
public interface EipCircuitBreakerRuleProxyService {

    String FUN_NAMESPACE = "eip.EipCircuitBreakerRuleProxyService";

    @Function
    void deleteOne(EipCircuitBreakerRuleProxy eipCircuitBreakerRuleProxy);

    @Function
    EipCircuitBreakerRuleProxy create(EipCircuitBreakerRuleProxy data);

    @Function
    Integer update(EipCircuitBreakerRuleProxy data);

    @Function
    Pagination<EipCircuitBreakerRuleProxy> queryPage(Pagination<EipCircuitBreakerRuleProxy> page, IWrapper<EipCircuitBreakerRuleProxy> queryWrapper);
}
