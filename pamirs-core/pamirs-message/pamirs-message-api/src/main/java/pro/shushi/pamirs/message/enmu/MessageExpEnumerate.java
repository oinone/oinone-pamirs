package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate;
import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "消息模块错误枚举")
public enum MessageExpEnumerate implements ExpBaseEnum {

    /**
     * @deprecated 6.3.0 please using {@link BaseExpEnumerate#BASE_USER_NOT_LOGIN_ERROR}
     */
    @Deprecated
    MAIL_USER_NOT_LOGIN(ERROR_TYPE.BIZ_ERROR, 20080002, "用户未登录"),

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060000, "系统异常"),
    MAIL_SQL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060001, "数据库查询异常"),
    MAIL_CLIENT_ARG_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060002, "前端传参异常"),
    MAIL_SMS_TEMPLATE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060003, "短信发送失败: 没有找到短信模板"),
    MAIL_SMS_TEMPLATE_CODE_CHANNEL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060004, "没有找到短信模板对应的发送渠道"),
    MAIL_EMAIL_VERIFY_TEMPLATE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060005, "发送邮箱验证码失败: 没有找到发送模板"),
    MAIL_SMS_VERIFY_CODE_SAVE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060006, "发送短信验证码失败: 保存短信验证码失败"),
    MAIL_SMS_VERIFY_CODE_UPDATE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060007, "发送短信验证码失败: 更新短信验证码失败"),
    MAIL_EMAIL_VERIFY_CODE_SAVE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060008, "发送邮箱验证码失败: 保存邮箱验证码失败"),
    MAIL_EMAIL_VERIFY_CODE_UPDATE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060009, "发送短信验证码失败: 更新邮箱验证码失败"),
    MAIL_EMAIL_REPLACE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060010, "邮件模板转换异常"),
    MAIL_NO_PARTNER_OR_MESSAGE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060011, "没有消息接收人或者未写入消息"),
    MAIL_NO_WECHATWORK_CONFIG_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060012, "企业微信配置消息错误"),
    MAIL_DDING_NO_PARTNER(ERROR_TYPE.SYSTEM_ERROR, 10060013, "请检查消息接收人是否有误"),
    MAIL_CONFIG_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060014, "短信模板配置异常"),
    MAIL_SEND_SMS_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060015, "短信请求异常"),
    MAIL_SENDER_CONFIG_SMTP_USER_REPEAT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060016, "SMTP用户名重复，该用户名已经创建"),
    MAIL_SENDER_CONFIG_SMTP_HOST_REPEAT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060017, "SMTP服务器地址重复，该地址已经创建"),
    EMAIL_VERIFY_TEMPLATE_TEMPLATE_TYPE_REPEAT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060018, "邮箱验证模版类型重复，该类型已经创建"),
    EMAIL_VERIFY_TEMPLATE_TITLE_REPEAT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060019, "邮箱标题重复，该标题已经创建"),
    MAIL_SENDER_CONFIG_SMTP_HOST_USER_REPEAT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060020, "SMTP服务器地址和用户名重复，该地址已经创建"),
    MAIL_SENDER_CONFIG_IS_NULL(ERROR_TYPE.SYSTEM_ERROR, 10060021, "未查询到可用的邮件服务器配置"),
    BIZ_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10060022, ""),
    CHANNEL_ISNOT_EXIST(ERROR_TYPE.SYSTEM_ERROR, 10060023, "无对应频道");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    MessageExpEnumerate(ERROR_TYPE type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }

    public ERROR_TYPE type() {
        return type;
    }

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }
}
