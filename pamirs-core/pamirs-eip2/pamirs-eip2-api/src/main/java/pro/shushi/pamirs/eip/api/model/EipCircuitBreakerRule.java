package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.eip.api.circuitbreaker.CircuitBreakerConfig;
import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerRecoveryStrategyEnum;
import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.List;

/**
 * @author yeshenyue on 2025/4/14 14:42.
 */
@Model(displayName = "熔断规则", labelFields = "ruleName")
@Model.model(EipCircuitBreakerRule.MODEL_MODEL)
@Model.Code(sequence = "ORDERLY_SEQ", prefix = "CBR", size = 8)
public class EipCircuitBreakerRule extends CodeModel {

    public static final String MODEL_MODEL = "eip.EipCircuitBreakerRule";
    private static final long serialVersionUID = -1100319125897444952L;

    @Field.String
    @Field(displayName = "规则名称", required = true, summary = "2~50个字符，仅支持中英文、数字、下划线")
    private String ruleName;

    @Field.Enum
    @Field(displayName = "熔断类型", required = true)
    private CircuitBreakerTypeEnum circuitBreakerType;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = {"circuitBreakerRuleCode"})
    @Field(displayName = "集成接口")
    private List<EipIntegrationInterface> integrationInterfaceList;

    @Field.Integer
    @Field(displayName = "统计时长", summary = "单位:秒")
    private Integer statisticalDuration;

    @Field.Integer
    @Field(displayName = "最小请求数目")
    private Integer minRequestCount;

    @Field.Integer
    @Field(displayName = "慢调用判定阈值", summary = "单位:ms")
    private Long slowCallResponseTime;

    @Field.Integer
    @Field(displayName = "慢调用熔断比例阈值", summary = "百分比")
    private Integer slowCallThreshold;

    @Field.Integer
    @Field(displayName = "异常熔断比例阈值", summary = "百分比")
    private Integer failureRateThreshold;

    @Field.Integer
    @Field(displayName = "熔断时长", summary = "单位:秒")
    private Integer circuitBreakerDuration;

    @Field.Enum
    @Field(displayName = "熔断恢复策略", required = true)
    private CircuitBreakerRecoveryStrategyEnum recoveryStrategy;

    public CircuitBreakerConfig buildConfig() {
        CircuitBreakerConfig.Builder config = CircuitBreakerConfig.builder()
                .statisticalDuration(getStatisticalDuration())
                .minRequestCount(getMinRequestCount())
                .waitDurationInOpenState(getCircuitBreakerDuration())
                .recoveryStrategy(getRecoveryStrategy());

        if (CircuitBreakerTypeEnum.EXCEPTION.equals(getCircuitBreakerType())) {
            config.failureRateThreshold(getFailureRateThreshold());
        } else if (CircuitBreakerTypeEnum.SLOW_CALL.equals(getCircuitBreakerType())) {
            config.slowCallResponseTime(getSlowCallResponseTime()).slowCallRateThreshold(getSlowCallThreshold());
        } else {
            throw PamirsException.construct(EipExpEnumerate.EIP_CB_TYPE_ERROR).errThrow();
        }
        return config.build();
    }
}
