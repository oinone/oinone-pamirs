package pro.shushi.pamirs.user.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "userLoginTypeEnum", displayName = "")
public enum UserLoginTypeEnum implements IEnum<String> {

    COOKIE("COOKIE", "cookie登录", "cookie登录"),
    TOKEN("TOKEN", "token登录", "token登录"),
    OAUTH("OAUTH", "服务免登调用", "服务免登调用");

    private String value;

    private String displayName;

    private String help;

    UserLoginTypeEnum(String value, String displayName, String help) {
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
    }
}
