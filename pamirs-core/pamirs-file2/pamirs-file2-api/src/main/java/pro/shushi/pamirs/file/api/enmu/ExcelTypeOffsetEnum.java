package pro.shushi.pamirs.file.api.enmu;

import org.apache.poi.ss.usermodel.Font;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExcelTypeOffsetEnum.dictionary, displayName = "Excel字体偏移类型")
public enum ExcelTypeOffsetEnum implements IEnum<String> {

    NORMAL("NORMAL", "常规", "常规", Font.SS_NONE),
    SUPER("SUPER", "上标", "上标", Font.SS_SUPER),
    SUB("SUB", "下标", "下标", Font.SS_SUB);

    public static final String dictionary = "file.ExcelTypeOffsetEnum";

    private String value;
    private String displayName;
    private String help;
    private short poi;

    ExcelTypeOffsetEnum(String value, String displayName, String help, short poi) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.poi = poi;
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

    public short getPoi() {
        return poi;
    }
}
