package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.Date;

/**
 * VerificationCode
 *
 * @author yakir on 2019/08/26 14:29.
 */
@Model.model(VerificationCode.MODEL_MODEL)
@Model.Advanced(name = "VerificationCode", index = "source,verifyType")
@Model(displayName = "验证码")
public class VerificationCode extends IdModel {

    public static final String MODEL_MODEL = "pamirs.message.VerificationCode";

    @Field.String(size = 256)
    @Field(displayName = "手机号/邮箱验证码")
    private String code;

    @Field.Text
    @Field(displayName = "手机号/邮箱验证码参数")
    private String params;

    @Field.String(size = 256)
    @Field(displayName = "手机号/邮箱", index = true)
    private String source;

    @Field.Enum
    @Field(displayName = "类型: 手机号/邮箱")
    private MessageEngineTypeEnum sourceType;

    @Field.Enum
    @Field(displayName = "验证码类型")
    private SMSTemplateTypeEnum verifyType;

    @Field.Boolean
    @Field(displayName = "验证码是否使用")
    private Boolean isUsed;

    @Field.Boolean
    @Field(displayName = "验证码是否已失效", defaultValue = "false")
    private Boolean invalid;

    @Field.String
    @Field(displayName = "外部订单号")
    private String outId;

    @Field.String
    @Field(displayName = "外部业务id")
    private String bizId;

    @Field.Date
    @Field(displayName = "过期时间")
    private Date expirationTime;

}
