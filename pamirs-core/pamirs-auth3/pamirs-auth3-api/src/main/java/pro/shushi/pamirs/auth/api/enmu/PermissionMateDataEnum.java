package pro.shushi.pamirs.auth.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 权限元数据类型
 *
 * @author shier
 * date  2020/7/10 4:40 下午
 */
@Deprecated
@Dict(dictionary = "PermissionMateDataEnum", displayName = "权限元数据类型")
public enum PermissionMateDataEnum implements IEnum<String> {

    SERVER_ACTION("SERVER_ACTION", "服务器动作", "服务器动作"),
    VIEW_ACTION("VIEW_ACTION", "窗口动作", "窗口动作"),
    URL_ACTION("URL_ACTION", "URL动作", "URL动作"),
    CLIENT_ACTION("CLIENT_ACTION", "客户端动作", "客户端动作"),
    MENU("MENU", "菜单", "菜单"),
    MODULE("MODULE", "模块", "模块"),
    MODEL_FIELD("MODEL_FIELD", "模型字段", "模型字段"),
    MODEL("MODEL", "模型", "模型"),
    FUNCTION("FUNCTION", "函数", "函数"),
    HOMEPAGE("HOMEPAGE", "首页", "首页"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    PermissionMateDataEnum(String value, String displayName, String help) {
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
