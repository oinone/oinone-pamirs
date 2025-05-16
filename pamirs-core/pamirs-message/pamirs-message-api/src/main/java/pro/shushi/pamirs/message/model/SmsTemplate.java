package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.message.enmu.SMSChannelEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateStatusEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * SMSTemplate
 *
 * @author yakir on 2019/08/23 14:11.
 */
@Model.Advanced(name = "SmsTemplate", unique = {"templateType,templateCode"})
@Model(displayName = "短信模板", labelFields = "name")
@Model.model(SmsTemplate.MODEL_MODEL)
public class SmsTemplate extends IdModel {

    private static final long serialVersionUID = 5521131467442633972L;

    public static final String MODEL_MODEL = "pamirs.message.SmsTemplate";

    @Field.String
    @Field(displayName = "名称")
    private String name;

    @Field.Text
    @Field(displayName = "备注")
    private String remark;

    @Field.Enum
    @Field(displayName = "短信通道")
    private SMSChannelEnum channel;

    @Field.Enum
    @Field(displayName = "短信模板类型", required = true)
    private SMSTemplateTypeEnum templateType;

    @Field.String
    @Field(displayName = "短信模板ID")
    private String templateCode;

    @Field.Text
    @Field(displayName = "短信模板内容")
    private String templateContent;

    @Field.Integer
    @Field(displayName = "验证码有效时间（秒/s）")
    private Integer timeInterval;

    @Field.Boolean
    @Field(displayName = "是否生成验证码", defaultValue = "false")
    private Boolean hasVerifyCode;

    @Field.Enum
    @Field(displayName = "审核状态", defaultValue = "PENDING_AUDIT")
    private SMSTemplateStatusEnum status;

    @Field.Text
    @Field(displayName = "审核备注", summary = "审核的具体原因")
    private String reason;

}
