package pro.shushi.pamirs.auth.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "AuthGroupTypeEnum", displayName = "权限组类型")
public enum AuthGroupTypeEnum implements IEnum<String> {

    MANAGEMENT("MANAGEMENT", "管理权限", "管理权限"),
    RUNTIME("RUNTIME", "运行权限", "运行权限"),
    FIELD("FIELD", "字段权限", "字段权限"),
    DATA("DATA", "数据权限", "数据权限"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    AuthGroupTypeEnum(String value, String displayName, String help) {
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
