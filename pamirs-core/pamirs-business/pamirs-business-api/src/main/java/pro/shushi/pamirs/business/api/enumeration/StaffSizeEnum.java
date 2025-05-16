package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * StaffSizeEnum
 *
 * @author yakir on 2022/09/13 15:50.
 */
@Dict(dictionary = StaffSizeEnum.dict, displayName = "人员规模")
public enum StaffSizeEnum implements IEnum<String> {

    SS_1_9("SS_1_9", "1-9", "1-9"),
    SS_10_20("SS_10_20", "10-20", "10-20"),
    SS_21_50("SS_21_50", "21-50", "21-50"),
    SS_51_100("SS_51_100", "51-100", "51-100"),
    SS_101_200("SS_101_200", "101-200", "101-200"),
    SS_201_500("SS_201_500", "201-500", "201-500"),
    SS_501_2000("SS_501_2000", "501-2000", "501-2000"),
    SS_2000("SS_2000", ">2000", ">2000"),

    ;

    public static final String dict = "business.StaffSizeEnum";


    private final String value;
    private final String displayName;
    private final String help;

    StaffSizeEnum(String value, String displayName, String help) {
        this.value       = value;
        this.displayName = displayName;
        this.help        = help;
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
