package pro.shushi.pamirs.message.model;


import pro.shushi.pamirs.message.enmu.EmailSendSecurityEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

@Model.Advanced(name = "EmailSenderSource", unique = {"smtpUser,smtpHost"})
@Model(displayName = "邮件服务器", labelFields = "name")
@Model.model(EmailSenderSource.MODEL_MODEL)
public class EmailSenderSource extends MessageSource {

    public static final String MODEL_MODEL = "pamirs.message.EmailSenderSource";

    // 配置文件初始化邮箱会填入默认值10，UI配置邮件服务器没有使用sequence，目前设计多个邮件服务器按创建时间来
    @Deprecated
    @Field.Integer
    @Field(displayName = "优先级", summary = "若邮件没有特地的邮件服务器被要求，最高优先级的服务器将被使用。默认优先级是10（数字越小，优先级越高）")
    private Integer sequence;

    @Field.String
    @Field(displayName = "用户")
    private String smtpUser;

    @Field.String
    @Field(displayName = "密码")
    private String smtpPassword;

    @Field.Enum
    @Field(displayName = "连接安全", summary = "选择连接加密方案：" +
            " None: SMTP 对话用明文完成。" +
            " TLS (STARTTLS): SMTP对话的开始时要求TLS 加密 (建议)" +
            " SSL/TLS: SMTP对话通过专用端口用 SSL/TLS 加密 (默认是: 465)")
    private EmailSendSecurityEnum smtpSecurity;

    @Field.String
    @Field(displayName = "smtp host")
    private String smtpHost;

    @Field.Integer
    @Field(displayName = "smtp 端口号")
    private Integer smtpPort;


    @Field.String
    @Field(displayName = "名称")
    private String name;
}
