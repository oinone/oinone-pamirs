package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "weekStartEnum", displayName = "")
public enum WeekStartEnum implements IEnum<String> {

    MONDAY("monday", "周一", "周一"),
    TUESDAY("tuesday", "周二", "周二"),
    WEDNESDAY("wednesday", "周三", "周三"),
    THURSDAY("thursday", "周四", "周四"),
    FRIDAY("friday", "周五", "周五"),
    SATURDAY("saturday", "周六", "周六"),
    SUNDAY("sunday", "周日", "周日");

    private String help;

    private String value;

    private String displayName;

    WeekStartEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}
