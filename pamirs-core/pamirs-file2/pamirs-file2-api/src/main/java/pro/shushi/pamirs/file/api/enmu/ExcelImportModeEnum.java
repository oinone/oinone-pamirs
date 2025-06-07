package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExcelImportModeEnum.dictionary, displayName = "Excel导入模式")
public enum ExcelImportModeEnum implements IEnum<String> {

    MULTI_MODEL("MULTI_MODEL", "多模型", "一个模板对应多个不同的sheet,默认模式"),
    SINGLE_MODEL("SINGLE_MODEL", "单模型", "所有sheet共用一个模型");

    public static final String dictionary = "file.ExcelImportModeEnum";

    private String value;
    private String displayName;
    private String help;

    ExcelImportModeEnum(String value, String displayName, String help) {
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
