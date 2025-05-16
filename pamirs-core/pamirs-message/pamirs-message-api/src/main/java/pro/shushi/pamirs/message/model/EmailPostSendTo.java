package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.message.enmu.EmailPostPartnerTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;


@Model(displayName = "邮件发送的用户")
@Model.model(EmailPostSendTo.MODEL_MODEL)
public class EmailPostSendTo extends IdModel implements MetaCheckConstants {

    public static final String MODEL_MODEL = "pamirs.message.EmailPostSendTo";

    @Field.String
    @Field(displayName = "名称")
    String name;

    @Field.Enum
    @Field(required = true, summary = "对象类型")
    private EmailPostPartnerTypeEnum type;

    @Field.many2one
    @Field(displayName = "用户")
    private PamirsPartner partner;

    @Field.String
    @Field(displayName = "固定邮箱地址")
    private String address;

    @Validation(check = checkFieldName)
    @Field.String
    @Field(summary = "模型的字段中的人员、例如创建者，上级等字段,该字段的value必须是用户id,model.aaa.sss格式")
    private String modelField;

}
