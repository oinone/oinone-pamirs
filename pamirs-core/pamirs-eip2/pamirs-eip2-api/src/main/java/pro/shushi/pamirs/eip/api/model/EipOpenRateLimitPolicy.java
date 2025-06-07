package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.eip.api.enmu.FlowControlEffectTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.common.CodeModel;

/**
 * @author yeshenyue on 2025/4/21 09:48.
 */
@Model(displayName = "开放接口流控策略")
@Model.model(EipOpenRateLimitPolicy.MODEL_MODEL)
@Model.Code(sequence = "SEQ", prefix = "RL")
@Model.Advanced(unique = {"applicationCode,interfaceName"})
public class EipOpenRateLimitPolicy extends CodeModel {

    public static final String MODEL_MODEL = "pamirs.eip.EipOpenRateLimitPolicy";
    private static final long serialVersionUID = 4524178538690651225L;

    @Field.String
    @Field(displayName = "应用编码")
    private String applicationCode;

    @Field.many2one
    @Field.Relation(relationFields = {"applicationCode"}, referenceFields = {"code"})
    @Field(displayName = "开放应用")
    private EipApplication application;

    @Field(displayName = "开放接口技术名称")
    @Field.String
    private String interfaceName;

    @Field(displayName = "开放接口")
    @Field.many2one
    @Field.Relation(relationFields = "interfaceName", referenceFields = "interfaceName")
    private EipOpenInterface openInterface;

    @Field.Integer
    @Field(displayName = "单机QPS阈值")
    private Long qps;

    @Field.Enum
    @Field(displayName = "流控效果")
    private FlowControlEffectTypeEnum flowControlEffect;

    @Field.Integer
    @Field(displayName = "超时时长")
    private Long timeout;
}
