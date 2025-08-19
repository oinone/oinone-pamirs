package pro.shushi.pamirs.eip.api.pmodel;

import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerTypeEnum;
import pro.shushi.pamirs.eip.api.model.strategy.EipCircuitBreakerRule;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * @author yeshenyue on 2025/4/14 15:06.
 */
@Model(displayName = "熔断配置代理")
@Model.model(EipCircuitBreakerRuleProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class EipCircuitBreakerRuleProxy extends EipCircuitBreakerRule {

    public static final String MODEL_MODEL = "eip.EipCircuitBreakerRuleProxy";
    private static final long serialVersionUID = 6037138005774256676L;

    @Field.String
    @Field(displayName = "熔断比例阈值")
    private String threshold;

    @Field.many2one
    @Field(displayName = "集成接口", summary = "用于查询")
    private EipIntegrationInterface integrationInterface;

    public String getThreshold() {
        if (CircuitBreakerTypeEnum.EXCEPTION.equals(getCircuitBreakerType())) {
            return String.valueOf(getFailureRateThreshold());
        } else if (CircuitBreakerTypeEnum.SLOW_CALL.equals(getCircuitBreakerType())) {
            return String.valueOf(getSlowCallThreshold());
        }
        return null;
    }
}
