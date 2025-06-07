package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author Wuxin
 * @Date 2024/7/3
 * @since 1.0
 */

@Base
@Dict(dictionary = BindingTypeEnum.dictionary, displayName = "绑定类型")
public enum BindingTypeEnum implements IEnum<String> {

    MENU("MENU", "绑定菜单", "绑定菜单"),
    VIEW("VIEW", "绑定视图", "绑定视图"),
    ;

    public static final String dictionary = "base.BindingTypeEnum";

    private String value;
    private String displayName;
    private String help;

    BindingTypeEnum(String value, String displayName, String help) {
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
