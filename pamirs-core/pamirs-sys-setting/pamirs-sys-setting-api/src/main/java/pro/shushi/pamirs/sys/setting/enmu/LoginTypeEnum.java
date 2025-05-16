package pro.shushi.pamirs.sys.setting.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * LoginTypeEnum
 *
 * @author yakir on 2022/09/15 13:58.
 */
@Dict(dictionary = LoginTypeEnum.dict, displayName = "认证状态")
public enum LoginTypeEnum implements IEnum<String> {

    NAME_PHONE("NAME_PHONE", "用户名或手机号登录（含小程序登录）", "用户名或手机号登录（含小程序登录）"),
    NAME_EMAIL("NAME_EMAIL", "用户名或邮箱登录（含小程序登录）", "用户名或邮箱登录（含小程序登录）"),
    NAME_PHONE_EMAIL_PV("NAME_PHONE_EMAIL_PV", "用户名/手机号/邮箱登录（含小程序登录），且验证码登录使用手机验证码", "用户名/手机号/邮箱登录（含小程序登录），且验证码登录使用手机验证码"),
    NAME_PHONE_EMAIL_EV("NAME_PHONE_EMAIL_EV", "用户名/手机号/邮箱登录（含小程序登录），且验证码登录使用邮箱验证码", "用户名/手机号/邮箱登录（含小程序登录），且验证码登录使用邮箱验证码"),
    NAME_PHONE_EMAIL_ALL("NAME_PHONE_EMAIL_ALL", "用户名/手机号/邮箱登录（含小程序登录），验证码登录可使用手机/邮箱验证码", "用户名/手机号/邮箱登录（含小程序登录），验证码登录可使用手机/邮箱验证码"),

    ;

    public static final String dict = "sysSetting.LoginTypeEnum";


    private final String value;
    private final String displayName;
    private final String help;

    LoginTypeEnum(String value, String displayName, String help) {
        this.value       = value;
        this.displayName = displayName;
        this.help        = help;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHelp() {
        return help;
    }
}
