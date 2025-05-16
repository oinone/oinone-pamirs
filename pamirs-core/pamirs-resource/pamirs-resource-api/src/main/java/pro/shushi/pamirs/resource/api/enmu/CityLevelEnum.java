package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = CityLevelEnum.dictionary, displayName = "城市等级")
public enum CityLevelEnum implements IEnum<String> {

    ONE("ONE", "一线", "一线"),
    NEW_ONE("NEW_ONE", "新一线", "新一线"),
    TWO("TWO", "二线", "二线"),
    THREE("THREE", "三线", "三线"),
    FOUR("FOUR", "四线", "四线"),
    FIVE("FIVE", "五线", "五线"),
    FIVE_DOWN("FIVE_DOWN", "五线以下", "五线以下"),
    ;

    public static final String dictionary = "resource.CityLevelEnum";

    private String value;
    private String displayName;
    private String help;

    CityLevelEnum(String value, String displayName, String help) {
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

    public static CityLevelEnum fetchByDisplayName(String displayName) {
        for (CityLevelEnum item : CityLevelEnum.values()) {
            if (item.displayName.equals(displayName)) {
                return item;
            }
        }
        return null;
    }
}
