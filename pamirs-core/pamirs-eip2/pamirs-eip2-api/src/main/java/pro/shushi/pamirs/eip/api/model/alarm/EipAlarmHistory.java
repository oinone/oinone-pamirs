package pro.shushi.pamirs.eip.api.model.alarm;

import pro.shushi.pamirs.eip.api.enmu.alarm.AlarmNotifyStatus;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.Date;

/**
 * EipAlarmHistory
 *
 * @author yakir on 2026/04/03 15:58.
 */
@Model(displayName = "告警记录")
@Model.model(EipAlarmHistory.MODEL_MODEL)
@Model.Advanced(index = {"interfaceName", "alertRuleName"})
public class EipAlarmHistory extends IdModel {

    private static final long serialVersionUID = -4286024258540938811L;

    public static final String MODEL_MODEL = "eip.EipAlarmHistory";

    @Field(displayName = "告警时间", summary = "规则被触发的时间")
    @Field.Date
    private Date alertTime;

    @Field(displayName = "接口技术名称", summary = "被告警的接口名称")
    @Field.String
    private String interfaceName;

    @Field(displayName = "告警规则")
    @Field.many2one
    @Field.Relation(relationFields = "interfaceName", referenceFields = "interfaceName")
    private EipIntegrationInterface eipInterface;

    @Field(displayName = "告警规则名称", summary = "被触发的告警规则名称")
    @Field.String
    private String alertRuleName;

    @Field(displayName = "告警规则")
    @Field.many2one
    @Field.Relation(relationFields = "alertRuleName", referenceFields = "name")
    private EipAlarmRule alarmRule;

    @Field(displayName = "通知状态")
    @Field.Enum
    private AlarmNotifyStatus notifyStatus;
}
