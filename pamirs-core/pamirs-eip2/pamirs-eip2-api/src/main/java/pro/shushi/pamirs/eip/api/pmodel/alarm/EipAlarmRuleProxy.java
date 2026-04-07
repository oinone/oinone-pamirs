package pro.shushi.pamirs.eip.api.pmodel.alarm;

import pro.shushi.pamirs.eip.api.model.alarm.EipAlarmRule;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * EipAlarmRuleProxy
 *
 * @author yakir on 2026/04/07 10:42.
 */
@Model(displayName = "集成接口告警规则代理")
@Model.model(EipAlarmRuleProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class EipAlarmRuleProxy extends EipAlarmRule {

    private static final long serialVersionUID = -912055098726376095L;

    public final static String MODEL_MODEL = "eip.EipAlarmRuleProxy";

    @Field.Integer
    @Field(displayName = "阈值", summary = "请输入失败次数阈值（正整数，范围：1-9999）")
    private Integer thresholdForCount;

    @Field.Integer
    @Field(displayName = "阈值", summary = "请输入失败率阈值（0~100%）")
    private Integer thresholdForRate;
}
