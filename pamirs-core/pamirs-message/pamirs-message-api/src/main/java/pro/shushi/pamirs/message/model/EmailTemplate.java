package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

@Model.Advanced(name = "EmailTemplate", unique = {"name"})
@Model(displayName = "邮件模板", labelFields = "name")
@Model.model(EmailTemplate.MODEL_MODEL)
public class EmailTemplate extends IdModel {

    public static final String MODEL_MODEL = "pamirs.message.EmailTemplate";

    @Field.String
    @Field(displayName = "名称")
    private String name;

    @Field.String
    @Field(displayName = "简介")
    private String description;

    @Field.String
    @Field(required = true, displayName = "邮件标题")
    private String title;

    @Field.Html
    @Field(required = true, displayName = "邮件内容")
    @Field.Advanced(columnDefinition = "MEDIUMTEXT")
    private String body;

    @Field.many2one
    @Field(displayName = "模型")
    @Field.Relation(relationFields = {"model"}, referenceFields = {"model"})
    private ModelDefinition modelDefinition;

    @Field.String
    @Field(displayName = "模型编码")
    private String model;

    @Field.many2one
    @Field(displayName = "邮件签名")
    private EmailUserSign emailUserSign;

    @Field.many2one
    @Field(displayName = "指定邮件服务器")
    private EmailSenderSource emailSenderSource;

}
