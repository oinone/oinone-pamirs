package pro.shushi.pamirs.message.tmodel;

import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

@Model.Advanced(name = "EmailPoster")
@Model(displayName = "邮箱发送")
@Model.model(EmailPoster.MODEL_MODEL)
public class EmailPoster extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.message.EmailPoster";

    @Field.String
    @Field(displayName = "主题", summary = "主题")
    String title;

    @Field.String
    @Field(displayName = "发件人名称", summary = "发件人名称")
    String sender;

    @Field.Text
    @Field(displayName = "内容", summary = "内容")
    String body;

    @Field.String
    @Field(displayName = "发送对象,逗号分隔", summary = "发送对象,逗号分隔")
    String sendTo;

    @Field.String
    @Field(displayName = "抄送对象,逗号分隔", summary = "抄送对象,逗号分隔")
    String copyTo;

    @Field.String
    @Field(displayName = "回复对象,逗号分隔", summary = "回复对象,逗号分隔")
    String replyTo;

    @Field.many2many(through = "EmailPosterRelationPamirsFile")
    @Field(displayName = "附件", summary = "附件")
    List<PamirsFile> resourceFiles;

}
