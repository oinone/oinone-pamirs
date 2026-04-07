package pro.shushi.pamirs.eip.api.model.alarm;

import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.eip.api.enmu.alarm.AlarmMetricType;
import pro.shushi.pamirs.eip.api.enmu.alarm.AlarmNotifyType;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.message.model.EmailTemplate;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.List;

/**
 * EipAlarmRule
 *
 * @author yakir on 2026/04/03 15:28.
 */
@Model(displayName = "集成接口告警规则")
@Model.model(EipAlarmRule.MODEL_MODEL)
@Model.Advanced(unique = "name")
public class EipAlarmRule extends IdModel {

    private static final long serialVersionUID = 1435562416513372506L;

    public final static String MODEL_MODEL = "eip.EipAlarmRule";

    @Field(displayName = "规则技术名称")
    @Field.String
    private String techName;

    @Field(displayName = "规则名称")
    @Field.String
    private String name;

    @Field(displayName = "指标类型")
    @Field.Enum
    private AlarmMetricType metricType;

    @Field.Integer
    @Field(displayName = "阈值", summary = "对应指标类型的阈值数值")
    private Integer threshold;

    @Field(displayName = "最小调用次数", summary = "仅当指标类型为失败率时显示，时间窗口内调用次数不足该值时不触发告警")
    @Field.Integer
    private Integer minCallCount;

    @Field(displayName = "时间窗口(秒)", summary = "N 秒内的统计窗口")
    @Field.Integer
    private Integer timeWindow;

    @Field(displayName = "重复发送间隔(分钟)", summary = "告警重复发送的间隔时间")
    @Field.Integer
    private Integer repeatInterval;

    @Field(displayName = "是否启用", defaultValue = "true")
    @Field.Boolean
    private Boolean enabled;

    @Field(displayName = "通知方式")
    @Field.Enum
    private AlarmNotifyType notifyType;

    @Field(displayName = "集成接口")
    @Field.Relation(relationFields = "techName", referenceFields = "interfaceName")
    @Field.many2many(through = EipAlarmRuleRelInterface.MODEL_MODEL, relationFields = "ruleTechName", referenceFields = "interfaceName")
    private List<EipIntegrationInterface> eipInterface;

    @Field(displayName = "告警接收人", summary = "邮件时必填，Webhook 时非必填，用于机器人消息中@指定人员")
    @Field.many2many
    private List<PamirsEmployee> receivers;

    @Field(displayName = "邮件模板", summary = "仅当通知方式为邮件时显示")
    @Field.many2one
    private EmailTemplate emailTemplate;

    @Field(displayName = "Webhook 地址", summary = "仅当通知方式为 Webhook 时显示，机器人的 Webhook 回调地址")
    @Field.String
    private String webhookUrl;

    @Field(displayName = "加签秘钥", summary = "仅当通知类型为钉钉机器人时显示")
    @Field.String
    private String signSecret;

    @Field(displayName = "通知内容", summary = "仅当通知方式为 Webhook 时显示，支持变量模板")
    @Field.Text
    private String notifyContent;

}
