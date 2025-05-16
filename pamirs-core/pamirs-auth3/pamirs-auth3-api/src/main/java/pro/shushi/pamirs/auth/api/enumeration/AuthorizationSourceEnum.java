package pro.shushi.pamirs.auth.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 权限数据来源
 *
 * @author Adamancy Zhang at 14:02 on 2024-01-04
 */
@Base
@Dict(dictionary = AuthorizationSourceEnum.DICTIONARY, displayName = "权限数据来源", summary = "权限数据来源")
public enum AuthorizationSourceEnum implements IEnum<String> {

    BUILD_IN("BUILD_IN", "内置", "内置不可修改"),
    SYSTEM("SYSTEM", "系统", "系统内置，允许用户修改"),
    MANUAL("MANUAL", "用户创建", "用户创建");

    public static final String DICTIONARY = "auth.AuthorizationSourceEnum";

    private final String value;
    private final String displayName;
    private final String help;

    AuthorizationSourceEnum(String value, String displayName, String help) {
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
