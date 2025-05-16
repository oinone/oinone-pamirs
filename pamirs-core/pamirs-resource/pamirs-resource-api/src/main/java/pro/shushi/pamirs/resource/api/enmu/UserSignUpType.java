package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "userSignUpType", displayName = "")
public enum UserSignUpType implements IEnum<String> {
    BYSELF("BYSELF", "自行注册", "自行注册"),
    BACKSTAGE("BACKSTAGE", "后台创建", "后台创建"),
    INVITE("INVITE", "邀请注册", "邀请注册");
    private String help;

    private String value;

    private String displayName;

    UserSignUpType(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}
