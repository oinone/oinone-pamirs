package pro.shushi.pamirs.eip.api.model.alarm;

import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.eip.api.enmu.alarm.AlarmMetricType;
import pro.shushi.pamirs.eip.api.enmu.alarm.AlarmNotifyStatus;
import pro.shushi.pamirs.eip.api.enmu.alarm.AlarmNotifyType;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;

import java.util.Date;

/**
 * EipAlarmHistory
 *
 * @author yakir on 2026/04/03 15:58.
 */
@Model(displayName = "告警记录")
@Model.model(EipAlarmHistory.MODEL_MODEL)
@Model.Advanced(index = {"interfaceName", "ruleTechName"})
@UxRouteButton(
        action = @UxAction(name = "redirectDetailPage", displayName = "详情", label = "详情", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = EipAlarmHistory.MODEL_MODEL, viewName = "detailView", openType = ActionTargetEnum.DIALOG))
public class EipAlarmHistory extends IdModel {

    private static final long serialVersionUID = -4286024258540938811L;

    public static final String MODEL_MODEL = "eip.EipAlarmHistory";

    @Field(displayName = "告警时间", summary = "规则被触发的时间")
    @Field.Date
    private Date alertTime;

    @Field(displayName = "接口名称", summary = "被告警的接口名称")
    @Field.String
    private String interfaceName;

    @Field(displayName = "接口技术名称", summary = "被告警的接口名称")
    @Field.String
    private String interfaceTechName;

    @Field(displayName = "告警规则")
    @Field.many2one
    @Field.Relation(relationFields = "interfaceName", referenceFields = "interfaceName")
    private EipIntegrationInterface eipInterface;

    @Field(displayName = "告警规则技术名称")
    @Field.String
    private String ruleTechName;

    @Field(displayName = "告警规则名称", summary = "被触发的告警规则名称")
    @Field.String
    private String ruleName;

    @Field(displayName = "告警规则")
    @Field.many2one
    @Field.Relation(relationFields = "ruleTechName", referenceFields = "techName")
    private EipAlarmRule alarmRule;

    @Field(displayName = "指标类型")
    @Field.Enum
    private AlarmMetricType metricType;

    @Field(displayName = "通知状态")
    @Field.Enum
    private AlarmNotifyStatus notifyStatus;

    @Field(displayName = "总调用次数")
    @Field.Integer
    private Long totalSum;

    @Field(displayName = "成功次数")
    @Field.Integer
    private Long successSum;

    @Field(displayName = "失败次数")
    @Field.Integer
    private Long failSum;

    @Field(displayName = "失败率")
    @Field.String
    private String failRate;

    @Field.String
    @Field(displayName = "阈值", summary = "对应指标类型的阈值数值")
    private String threshold;

    @Field(displayName = "时间窗口")
    @Field.String
    private String timeWindow;

    @Field(displayName = "通知方式")
    @Field.Enum
    private AlarmNotifyType notifyType;

    @Field(displayName = "异常消息")
    @Field.Text
    private String errorMsg;
}
