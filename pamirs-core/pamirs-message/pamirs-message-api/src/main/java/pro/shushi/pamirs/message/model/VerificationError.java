package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * @author haibo(xf.z @ shushi.pro)
 * @date 2022-09-21 15:20:59
 */
@Model.model(VerificationError.MODEL_MODEL)
@Model.Advanced(name = "VerificationError", unique = {"source,sourceType,verifyType"})
@Model(displayName = "验证码错误信息")
public class VerificationError extends IdModel {

    public static final String MODEL_MODEL = "pamirs.message.VerificationError";

    @Field.String(size = 256)
    @Field(displayName = "手机号/邮箱", index = true)
    private String source;

    @Field.Enum
    @Field(displayName = "类型: 手机号/邮箱")
    private MessageEngineTypeEnum sourceType;

    @Field.Enum
    @Field(displayName = "验证码类型")
    private SMSTemplateTypeEnum verifyType;

    @Field.Integer
    @Field(displayName = "输错次数", defaultValue = "0")
    private Integer errorNum;

}
