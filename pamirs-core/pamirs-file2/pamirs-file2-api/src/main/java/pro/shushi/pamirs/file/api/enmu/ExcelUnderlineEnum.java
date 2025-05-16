package pro.shushi.pamirs.file.api.enmu;

import org.apache.poi.ss.usermodel.Font;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExcelUnderlineEnum.dictionary, displayName = "Excel字体风格类型")
public enum ExcelUnderlineEnum implements IEnum<String> {

    NONE("NONE", "无", "无", Font.U_NONE),
    SINGLE("SINGLE", "单下划线", "上标", Font.U_SINGLE),
    DOUBLE("DOUBLE", "双下划线", "上标", Font.U_DOUBLE),
    SINGLE_ACCOUNTING("SINGLE_ACCOUNTING", "会计风格单下划线", "上标", Font.U_SINGLE_ACCOUNTING),
    DOUBLE_ACCOUNTING("DOUBLE_ACCOUNTING", "会计风格双下划线", "下标", Font.U_DOUBLE_ACCOUNTING);

    public static final String dictionary = "file.ExcelUnderlineEnum";

    private String value;
    private String displayName;
    private String help;
    private byte poi;

    ExcelUnderlineEnum(String value, String displayName, String help, byte poi) {
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

    public byte getPoi() {
        return poi;
    }
}
