package pro.shushi.pamirs.eip.api.model.alarm;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdRelation;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * EipAlarmRuleRelEmployee
 *
 * @author yakir on 2026/04/03 20:36.
 */
@Model(displayName = "告警规则员工关系表")
@Model.model(EipAlarmRuleRelEmployee.MODEL_MODEL)
@Model.Advanced(relationship = NullableBoolEnum.TRUE)
public class EipAlarmRuleRelEmployee extends IdRelation {

    private static final long serialVersionUID = -4190866665406504655L;

    public final static String MODEL_MODEL = "eip.EipAlarmRuleRelEmployee";

    @Field(displayName = "规则名称")
    @Field.String
    private String ruleTechName;

    @Field(displayName = "员工编码")
    @Field.String
    private String employeeCode;
}
