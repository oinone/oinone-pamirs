package pro.shushi.pamirs.eip.api.strategy.service;

import pro.shushi.pamirs.eip.api.model.strategy.EipCircuitBreakerRule;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * @author yeshenyue on 2025/4/16 15:01.
 */
@Fun(EipCircuitBreakerRuleService.FUN_NAMESPACE)
public interface EipCircuitBreakerRuleService {

    String FUN_NAMESPACE = "eip.EipCircuitBreakerRuleService";

    @Function
    void register(String interfaceName);

    @Function
    void create(EipCircuitBreakerRule data);

    @Function
    Integer update(EipCircuitBreakerRule data);

    @Function
    void deleteOne(EipCircuitBreakerRule eipCircuitBreakerRule);

    @Function
    void init();
}
