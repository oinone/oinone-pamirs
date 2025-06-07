package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "calendartypeenum", displayName = "日历枚举")
public enum CalendarTypeEnum implements IEnum<String> {
    Gregorian("Gregorian", "格里高利历", "格里高利历"),
    Lunar("Lunar", "农历", "农历"),
    Solar("Solar", "阳历", "阳历");

    private String help;

    private String value;

    private String displayName;

    CalendarTypeEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}
