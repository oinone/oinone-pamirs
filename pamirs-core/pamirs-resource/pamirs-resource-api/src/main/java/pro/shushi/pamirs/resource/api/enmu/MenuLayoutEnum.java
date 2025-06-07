package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "menuLayoutEnum", displayName = "")
public enum MenuLayoutEnum implements IEnum<String> {
    HORIZONTAL_MENU("horizontalMenu", "横向菜单", "横向菜单"),
    VERTICAL_MENU("verticalMenu", "纵向菜单", "纵向菜单"),
    ;

    private String help;

    private String value;

    private String displayName;

    MenuLayoutEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}
