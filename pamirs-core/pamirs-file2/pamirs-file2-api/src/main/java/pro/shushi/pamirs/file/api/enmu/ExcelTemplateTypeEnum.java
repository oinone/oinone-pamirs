package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExcelTemplateTypeEnum.dictionary, displayName = "Excel模板类型")
public enum ExcelTemplateTypeEnum implements IEnum<String> {

    IMPORT_EXPORT("IMPORT_EXPORT", "全部", "全部"),
    IMPORT("IMPORT", "导入", "仅用作导入"),
    EXPORT("EXPORT", "导出", "仅用作导出");

    public static final String dictionary = "file.ExcelTemplateTypeEnum";

    private String value;

    private String displayName;

    private String help;

    ExcelTemplateTypeEnum(String value, String displayName, String help) {
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

