package pro.shushi.pamirs.user.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author haibo(xf.z@shushi.pro)
 * @date 2022-09-16 14:31:27
 */
@Dict(dictionary = "user.enum.userBehaviorEventEnum", displayName = "用户行为事件")
public enum UserBehaviorEventEnum implements IEnum<String> {

    MODIFY_PASSWORD_BY_PHONE("MODIFY_PASSWORD_BY_PHONE", "手机验证修改密码", "手机验证修改密码"),
    MODIFY_PASSWORD_BYPHONE_SEND_CODE("MODIFY_PASSWORD_BYPHONE_SEND_CODE", "发送修改手机密码验证码", "发送修改手机密码验证码"),

    MODIFY_PHONE("MODIFY_PHONE", "修改手机号", "修改手机号"),
    MODIFY_PHONE_OLD_PHONE_CODE("MODIFY_PHONE_OLD_PHONE_CODE", "发送修改手机号-原手机验证码", "发送修改手机号-原手机验证码"),
    MODIFY_PHONE_NEW_PHONE_CODE("MODIFY_PHONE_NEW_PHONE_CODE", "发送修改手机号-新手机验证码", "发送修改手机号-新手机验证码"),
    MODIFY_PHONE_SEND_EMAIL("MODIFY_PHONE_SEND_EMAIL", "发送修改手机号-邮箱验证码", "发送修改手机号-邮箱验证码"),

    MODIFY_PASSWORD_BY_EMAIL("MODIFY_PASSWORD_BY_EMAIL", "邮箱验证修改密码", "邮箱验证修改密码"),
    MODIFY_PASSWORD_SEND_RESET_EMAIL("MODIFY_PASSWORD_SEND_RESET_EMAIL", "发送重置密码邮件", "发送重置密码邮件"),

    MODIFY_EMAIL("MODIFY_EMAIL", "修改邮箱", "修改邮箱"),
    MODIFY_EMAIL_SEND_OLD_EMAIL("MODIFY_EMAIL_SEND_OLD_EMAIL", "修改邮箱-发送原邮箱确认邮件", "修改邮箱-发送原邮箱确认邮件"),
    MODIFY_EMAIL_SEND_NEW_EMAIL("MODIFY_EMAIL_SEND_NEW_EMAIL", "修改邮箱-发送新邮箱确认邮件", "修改邮箱-发送新邮箱确认邮件"),
    MODIFY_EMAIL_SEND_PHONE_CODE("MODIFY_EMAIL_SEND_PHONE_CODE", "修改邮箱-手机验证码", "修改邮箱-手机验证码"),


    ADD_CORP_SEND_PHONE_CODE("ADD_CORP_SEND_PHONE_CODE", "加入团队手机验证码", "加入团队手机验证码"),
    ADD_CORP_SEND_EMAIL_CODE("ADD_CORP_SEND_EMAIL_CODE", "加入团队邮件验证码", "加入团队邮件验证码"),

    SIGN_UP_PHONE("SIGN_UP_PHONE", "手机号注册", "手机号注册"),
    SIGN_UP_EMAIL("SIGN_UP_EMAIL", "邮箱注册", "邮箱注册"),

    LOGIN_BY_WECHAT_MA("LOGIN_BY_WECHAT_MA", "微信小程序登录", "微信小程序登录"),

    LOGIN_BY_PHONE_CODE("LOGIN_BY_PHONE_CODE", "手机验证码登录", "手机验证码登录"),
    SEND_LOGIN_BY_PHONE_CODE("SEND_LOGIN_BY_PHONE_CODE", "发送手机验证码", "发送手机验证码"),

    LOGIN_BY_EMAIL_CODE("LOGIN_BY_EMAIL_CODE", "邮箱验证码登录", "邮箱验证码登录"),
    SEND_LOGIN_BY_EMAIL_CODE("SEND_LOGIN_BY_EMAIL_CODE", "发送邮箱验证码", "发送邮箱验证码");

    private String value;

    private String displayName;

    private String help;

    UserBehaviorEventEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHelp() {
        return help;
    }}
