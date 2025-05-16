package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 导出文件类型
 *
 * @author Adamancy Zhang at 18:15 on 2024-03-28
 */
@Base
@Dict(dictionary = ExcelExportFileTypeEnum.DICTIONARY, displayName = "导出文件类型", summary = "系统支持的全部导出文件类型")
public enum ExcelExportFileTypeEnum implements IEnum<String> {

    EXCEL("EXCEL", "Excel格式", "导出Excel格式文件"),
    CSV("CSV", "CSV格式", "导出CSV格式文件"),
    ;

    public static final String DICTIONARY = "file.ExcelExportFileTypeEnum";

    private final String value;
    private final String displayName;
    private final String help;

    ExcelExportFileTypeEnum(String value, String displayName, String help) {
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
