package pro.shushi.pamirs.file.api.enmu;

import com.alibaba.excel.enums.WriteDirectionEnum;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExcelDirectionEnum.dictionary, displayName = "Excel排列方向")
public enum ExcelDirectionEnum implements IEnum<String> {

    HORIZONTAL("HORIZONTAL", "水平排列", "子元素水平排列，垂直填充", WriteDirectionEnum.VERTICAL),
    VERTICAL("VERTICAL", "垂直排列", "子元素垂直排列，水平填充", WriteDirectionEnum.HORIZONTAL);

    public static final String DICTIONARY = "file.ExcelDirectionEnum";

    @Deprecated
    public static final String dictionary = DICTIONARY;

    private final String value;
    private final String displayName;
    private final String help;
    private final WriteDirectionEnum easyExcelWriteDirection;

    ExcelDirectionEnum(String value, String displayName, String help, WriteDirectionEnum easyExcelWriteDirection) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.easyExcelWriteDirection = easyExcelWriteDirection;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }

    public WriteDirectionEnum getEasyExcelWriteDirection() {
        return easyExcelWriteDirection;
    }

    @Deprecated
    public String getValue() {
        return value;
    }

    @Deprecated
    public String getDisplayName() {
        return displayName;
    }

    @Deprecated
    public String getHelp() {
        return help;
    }
}
