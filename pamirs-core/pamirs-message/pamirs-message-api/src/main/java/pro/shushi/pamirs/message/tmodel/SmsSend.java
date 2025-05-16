package pro.shushi.pamirs.message.tmodel;

import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * @author xzf 2021/03/08 15:43
 **/
@Model.model(SmsSend.MODEL_MODEL)
@Model(displayName = "短信验证码通用模型", labelFields = "code")
public class SmsSend extends TransientModel {

    public final static String MODEL_MODEL = "pamirs.message.SmsSend";

    @Field.String
    @Field(displayName = "手机号", summary = "接收手机号")
    private String phone;

    @Field.String
    @Field(displayName = "短信验证码")
    private String code;

    @Field.Enum
    @Field(displayName = "短信验证码类型")
    private SMSTemplateTypeEnum type;

}