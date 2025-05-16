package pro.shushi.pamirs.auth.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 资源子类型
 *
 * @author Adamancy Zhang at 14:50 on 2024-01-04
 */
@Base
@Dict(dictionary = ResourcePermissionSubtypeEnum.DICTIONARY, displayName = "资源子类型", summary = "资源子类型")
public enum ResourcePermissionSubtypeEnum implements IEnum<String> {

    MODULE("MODULE", "模块", "模块"),
    HOMEPAGE("HOMEPAGE", "首页", "首页"),
    MENU("MENU", "菜单", "菜单"),
    VIEW("VIEW", "视图", "视图"),
    SERVER_ACTION("SERVER_ACTION", "服务器动作", "服务器动作"),
    VIEW_ACTION("VIEW_ACTION", "窗口动作", "窗口动作"),
    URL_ACTION("URL_ACTION", "URL动作", "URL动作"),
    CLIENT_ACTION("CLIENT_ACTION", "客户端动作", "客户端动作"),
    FUNCTION("FUNCTION", "函数", "函数"),
    CUSTOM("CUSTOM", "自定义资源", "自定义资源");

    public static final String DICTIONARY = "auth.ResourcePermissionSubtypeEnum";

    private final String value;
    private final String displayName;
    private final String help;

    ResourcePermissionSubtypeEnum(String value, String displayName, String help) {
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
