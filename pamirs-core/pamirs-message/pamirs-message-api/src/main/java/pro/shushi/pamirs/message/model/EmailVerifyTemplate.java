package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

@Model.Advanced(name = "EmailVerifyTemplate", unique = {"title", "templateType"})
@Model(displayName = "验证邮件模板", labelFields = "name")
@Model.model(EmailVerifyTemplate.MODEL_MODEL)
public class EmailVerifyTemplate extends IdModel {

    public static final String MODEL_MODEL = "pamirs.message.EmailVerifyTemplate";

    @Field.String
    @Field(required = true, displayName = "邮件标题")
    private String title;

    @Field.Html
    @Field(required = true, displayName = "邮件内容")
    private String body;

    @Field.Enum
    @Field(displayName = "邮件验证模板类型")
    private SMSTemplateTypeEnum templateType;

    @Field.Integer
    @Field(displayName = "验证码有效时间（秒/s）")
    private Integer timeInterval;

}
