package pro.shushi.pamirs.eip.api.model.statistics;

import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.strategy.EipCircuitBreakerRule;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.Date;

/**
 * @author yeshenyue on 2025/4/15 14:22.
 */
@Model(displayName = "接口熔断记录")
@Model.model(CircuitBreakerRecord.MODEL_MODEL)
@Model.Advanced(index = {"interfaceName", "circuitBreakerRuleCode", "circuitBreakerRuleName"})
public class CircuitBreakerRecord extends IdModel {

    public static final String MODEL_MODEL = "eip.CircuitBreakerRecord";
    private static final long serialVersionUID = 2338100374587114493L;

    @Field.String
    @Field(displayName = "接口技术名称")
    private String interfaceName;

    @Field.many2one
    @Field.Relation(relationFields = {"interfaceName"}, referenceFields = {"interfaceName"})
    @Field(displayName = "集成接口")
    private EipIntegrationInterface integrationInterface;

    @Field.Date
    @Field(displayName = "熔断开始时间")
    private Date startTime;

    @Field.Date
    @Field(displayName = "熔断结束时间")
    private Date endTime;

    @Field.String
    @Field(displayName = "熔断器规则编码")
    private String circuitBreakerRuleCode;

    @Field.many2one
    @Field.Advanced(columnDefinition = "text")
    @Field.Relation(store = false)
    @Field(displayName = "熔断器规则", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private EipCircuitBreakerRule circuitBreakerRule;

    @Field.String
    @Field(displayName = "熔断器规则名称")
    private String circuitBreakerRuleName;
}
