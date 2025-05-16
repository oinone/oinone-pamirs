package pro.shushi.pamirs.auth.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author shier
 * date  2020/6/23 4:40 下午
 */
@Deprecated
@Dict(dictionary = "PermissionDataSourceTypeEnum", displayName = "权限项数据来源")
public enum PermissionDataSourceEnum implements IEnum<String> {

    BUILD_IN("BUILD_IN", "内置", "内置"),
    SYSTEM("SYSTEM", "系统", "系统"),
    CUSTOM("CUSTOM", "自定义", "自定义"),
    INITIALIZATION("INITIALIZATION", "初始化", "初始化"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    PermissionDataSourceEnum(String value, String displayName, String help) {
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
