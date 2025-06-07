package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "timeZoneTypeEnum", displayName = "")
public enum TimeZoneTypeEnum implements IEnum<String> {

    Australia_Darwin("Australia/Darwin", "Australia/Darwin", "Australia/Darwin"),
    Australia_Sydney("Australia/Sydney", "Australia/Sydney", "Australia/Sydney"),
    America_Argentina_Buenos_Aires("America/Argentina/Buenos_Aires", "America/Argentina/Buenos_Aires", "America/Argentina/Buenos_Aires"),
    Africa_Cairo("Africa/Cairo", "Africa/Cairo", "Africa/Cairo"),
    America_Anchorage("America/Anchorage", "America/Anchorage", "America/Anchorage"),
    America_Sao_Paulo("America/Sao_Paulo", "America/Sao_Paulo", "America/Sao_Paulo"),
    Asia_Dhaka("Asia/Dhaka", "Asia/Dhaka", "Asia/Dhaka"),
    Africa_Harare("Africa/Harare", "Africa/Harare", "Africa/Harare"),
    America_St_Johns("America/St_Johns", "America/St_Johns", "America/St_Johns"),
    America_Chicago("America/Chicago", "America/Chicago", "America/Chicago"),
    Asia_Shanghai("Asia/Shanghai", "Asia/Shanghai", "Asia/Shanghai"),
    Africa_Addis_Ababa("Africa/Addis_Ababa", "Africa/Addis_Ababa", "Africa/Addis_Ababa"),
    Europe_Paris("Europe/Paris", "Europe/Paris", "Europe/Paris"),
    America_Indiana_Indianapolis("America/Indiana/Indianapolis", "America/Indiana/Indianapolis", "America/Indiana/Indianapolis"),
    Asia_Kolkata("Asia/Kolkata", "Asia/Kolkata", "Asia/Kolkata"),
    Asia_Tokyo("Asia/Tokyo", "Asia/Tokyo", "Asia/Tokyo"),
    Pacific_Apia("Pacific/Apia", "Pacific/Apia", "Pacific/Apia"),
    Asia_Yerevan("Asia/Yerevan", "Asia/Yerevan", "Asia/Yerevan"),
    Pacific_Auckland("Pacific/Auckland", "Pacific/Auckland", "Pacific/Auckland"),
    Asia_Karachi("Asia/Karachi", "Asia/Karachi", "Asia/Karachi"),
    America_Phoenix("America/Phoenix", "America/Phoenix", "America/Phoenix"),
    America_Puerto_Rico("America/Puerto_Rico", "America/Puerto_Rico", "America/Puerto_Rico");

    private String help;

    private String value;

    private String displayName;

    TimeZoneTypeEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


    public static TimeZoneTypeEnum getEnumByValue(String value) {
        for (TimeZoneTypeEnum timeZoneTypeEnum : TimeZoneTypeEnum.values()) {
            if (timeZoneTypeEnum.value().equals(value)) {
                return timeZoneTypeEnum;
            }
        }
        return null;
    }

}
