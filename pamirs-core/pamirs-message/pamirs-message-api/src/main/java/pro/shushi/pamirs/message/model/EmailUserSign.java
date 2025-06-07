package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

@Model.Advanced(name = "EmailUserSign", unique = {"name"})
@Model(displayName = "邮件签名", labelFields = "name")
@Model.model(EmailUserSign.MODEL_MODEL)
public class EmailUserSign extends IdModel {

    public static final String MODEL_MODEL = "pamirs.message.EmailUserSign";

    @Field.many2one
    @Field(displayName = "用户")
    private PamirsPartner partner;

    @Field.String
    @Field(displayName = "用户名")
    @Field.Related(related = {"partner", "name"})
    private String partnerName;

    @Field.String
    @Field(required = true, displayName = "邮件签名名称")
    private String name;

    @Field.Html(size = 4096)
    @Field(required = true, displayName = "邮件签名")
    private String emailSignature;

}
