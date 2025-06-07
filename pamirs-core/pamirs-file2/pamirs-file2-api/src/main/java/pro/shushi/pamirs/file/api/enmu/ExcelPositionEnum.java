package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExcelPositionEnum.dictionary, displayName = "Excel定位属性")
public enum ExcelPositionEnum implements IEnum<String> {

    ABSOLUTELY("ABSOLUTELY", "绝对定位", "绝对定位"),
    RELATIVE("RELATIVE", "相对定位", "相对定位");

    public static final String dictionary = "file.ExcelPositionEnum";

    private String value;
    private String displayName;
    private String help;

    ExcelPositionEnum(String value, String displayName, String help) {
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
