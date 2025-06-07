package pro.shushi.pamirs.file.api.enmu;

import com.alibaba.excel.support.ExcelTypeEnum;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = OfficeVersionEnum.dictionary, displayName = "Office版本")
public enum OfficeVersionEnum implements IEnum<String> {

    AUTO("AUTO", "自动识别", "自动识别文件类型，无法识别时会出错", ExcelTypeEnum.XLSX),
    OLD("OLD", "旧版", "指2003年及之前发行的office版本，使用旧的文件后缀", ExcelTypeEnum.XLS),
    NEW("NEW", "新版", "指2003年之后发行的office版本，使用新的文件后缀", ExcelTypeEnum.XLSX)
    // FIXME: zbh 20241104 未来需要支持CSV模板定义，暂不开放
    // CSV("CSV", "CSV", "CSV", ExcelTypeEnum.CSV),
    ;

    public static final String dictionary = "file.OfficeVersionEnum";

    private final String value;
    private final String displayName;
    private final String help;
    private final ExcelTypeEnum excelType;

    OfficeVersionEnum(String value, String displayName, String help, ExcelTypeEnum excelType) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.excelType = excelType;
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

    public ExcelTypeEnum getExcelType() {
        return excelType;
    }
}
