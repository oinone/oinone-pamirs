package pro.shushi.pamirs.auth.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 资源类型
 *
 * @author Adamancy Zhang at 14:47 on 2024-01-04
 */
@Base
@Dict(dictionary = ResourcePermissionTypeEnum.DICTIONARY, displayName = "资源类型", summary = "资源类型")
public enum ResourcePermissionTypeEnum implements IEnum<String> {

    MODULE("MODULE", "模块", "模块"),
    MENU("MENU", "菜单", "菜单"),
    VIEW("VIEW", "视图", "视图"),
    ACTION("ACTION", "动作", "动作"),
    FUNCTION("FUNCTION", "函数", "函数"),
    CUSTOM("CUSTOM", "自定义资源", "自定义资源");

    public static final String DICTIONARY = "auth.ResourcePermissionTypeEnum";

    private final String value;
    private final String displayName;
    private final String help;

    ResourcePermissionTypeEnum(String value, String displayName, String help) {
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
