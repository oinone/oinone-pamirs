package pro.shushi.pamirs.user.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 用户数据来源
 *
 * @author Adamancy Zhang at 14:02 on 2024-01-04
 */
@Base
@Dict(dictionary = UserSourceEnum.DICTIONARY, displayName = "用户数据来源", summary = "用户数据来源")
public enum UserSourceEnum implements IEnum<String> {

    BUILD_IN("BUILD_IN", "内置", "内置"),
    SYSTEM("SYSTEM", "系统", "系统"),
    MANUAL("MANUAL", "用户创建", "用户创建"),
    THIRD_PARTY("THIRD_PARTY", "第三方", "第三方");

    public static final String DICTIONARY = "user.UserSourceEnum";

    private final String value;
    private final String displayName;
    private final String help;

    UserSourceEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }
}
