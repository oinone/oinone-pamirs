package pro.shushi.pamirs.eip.api.model.alarm;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdRelation;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * EipAlarmRuleRelInterface
 *
 * @author yakir on 2026/04/03 20:36.
 */
@Model(displayName = "告警规则集成接口关系表")
@Model.model(EipAlarmRuleRelInterface.MODEL_MODEL)
@Model.Advanced(relationship = NullableBoolEnum.TRUE)
public class EipAlarmRuleRelInterface extends IdRelation {

    private static final long serialVersionUID = -3666697722794902351L;

    public final static String MODEL_MODEL = "eip.EipAlarmRuleRelInterface";

    @Field(displayName = "规则名称")
    @Field.String
    private String ruleTechName;

    @Field(displayName = "接口名称")
    @Field.String
    private String interfaceName;
}
