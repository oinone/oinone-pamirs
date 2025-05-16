package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "resource.NamePositionEnum", displayName = "")
public enum NamePositionEnum implements IEnum<String> {

    BEFORE("BEFORE", "first name在前", "first name在前"),
    AFTER("AFTER", "last name在后", "last name在后");


    private String help;

    private String value;

    private String displayName;

    NamePositionEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}
