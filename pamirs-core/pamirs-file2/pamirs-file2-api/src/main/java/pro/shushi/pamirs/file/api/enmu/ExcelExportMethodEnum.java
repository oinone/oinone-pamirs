package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 导出方式
 *
 * @author Adamancy Zhang at 19:12 on 2024-08-02
 */
@Base
@Dict(dictionary = ExcelExportMethodEnum.DICTIONARY, displayName = "导出方式", summary = "导出方式")
public enum ExcelExportMethodEnum implements IEnum<String> {

    TEMPLATE("TEMPLATE", "根据模板导出", "根据模板导出"),
    SELECT_TEMPLATE_FIELD("SELECT_TEMPLATE_FIELD", "根据模板选择字段导出", "根据模板选择字段导出"),
    SELECT_FIELD("SELECT_FIELD", "根据模型选择字段导出", "根据模型选择字段导出"),
    ;

    public static final String DICTIONARY = "file.ExcelExportMethodEnum";

    private final String value;
    private final String displayName;
    private final String help;

    ExcelExportMethodEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
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
}
