package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;

/**
 * SMSTemplateTypeEnum 短信类型
 *
 * @author yakir on 2019/08/23 14:05.
 */
@Dict(dictionary = SMSTemplateTypeEnum.dictionary, displayName = "短信类型")
public class SMSTemplateTypeEnum extends BaseEnum<SMSTemplateTypeEnum, String> {

    public static final String dictionary = "mail.enmu.SMSTemplateTypeEnum";

    public final static SMSTemplateTypeEnum SIGN_IN = create("SIGN_IN", "SIGN_IN", "登录确认", "登录确认");
    public final static SMSTemplateTypeEnum SIGN_UP = create("SIGN_UP", "SIGN_UP", "用户注册", "用户注册");
    public final static SMSTemplateTypeEnum CHANGE_PWD = create("CHANGE_PWD", "CHANGE_PWD", "修改密码", "修改密码");
    public final static SMSTemplateTypeEnum CHANGE_PHONE = create("CHANGE_PHONE", "CHANGE_PHONE", "修改手机号", "修改手机号");
    public final static SMSTemplateTypeEnum CHANGE_EMAIL = create("CHANGE_EMAIL", "CHANGE_EMAIL", "修改邮箱", "修改邮箱");
    public final static SMSTemplateTypeEnum NEW_PHONE = create("NEW_PHONE", "NEW_PHONE", "新手机号验证码", "新手机号验证码");
    public final static SMSTemplateTypeEnum NEW_EMAIL = create("NEW_EMAIL", "NEW_EMAIL", "新邮箱验证码", "新邮箱验证码");
    public final static SMSTemplateTypeEnum ADD_CORP = create("ADD_CORP", "ADD_CORP", "加入团队", "加入团队");
//    public final static SMSTemplateTypeEnum OLD_PHONE = create("OLD_PHONE", "OLD_PHONE", "原手机号验证码", "原手机号验证码");
//    public final static SMSTemplateTypeEnum OLD_EMAIL = create("OLD_EMAIL", "OLD_EMAIL", "原邮箱验证码", "原邮箱验证码");
//    public final static SMSTemplateTypeEnum RESET_EMAIL = create("RESET_EMAIL", "RESET_EMAIL", "重置密码邮件", "重置密码邮件");
    public final static SMSTemplateTypeEnum NOTIFY = create("NOTIFY", "NOTIFY", "通知短信", "通知短信");
    public final static SMSTemplateTypeEnum PAY = create("PAY", "PAY", "支付验证码", "支付验证码");

}
