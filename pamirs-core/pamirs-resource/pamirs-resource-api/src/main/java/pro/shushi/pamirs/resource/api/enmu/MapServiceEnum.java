package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mapserviceenum", displayName = "")
public enum MapServiceEnum implements IEnum<String> {
    GAODE("gaode", "高德", "高德"),

    ;

    private String help;

    private String value;

    private String displayName;

    MapServiceEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}
