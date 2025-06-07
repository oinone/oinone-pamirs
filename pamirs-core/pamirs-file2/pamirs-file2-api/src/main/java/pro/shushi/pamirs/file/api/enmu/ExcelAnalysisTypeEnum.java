package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * Excel解析类型
 *
 * @author Adamancy Zhang at 12:18 on 2024-06-11
 */
@Base
@Dict(dictionary = ExcelAnalysisTypeEnum.DICTIONARY, displayName = "Excel解析类型")
public enum ExcelAnalysisTypeEnum implements IEnum<String> {

    FIXED_HEADER("FIXED_HEADER", "固定表头", "固定表头"),
    FIXED_FORMAT("FIXED_FORMAT", "固定格式", "固定格式");

    public static final String DICTIONARY = "file.ExcelAnalysisTypeEnum";

    @Deprecated
    public static final String dictionary = DICTIONARY;

    private final String value;
    private final String displayName;
    private final String help;

    ExcelAnalysisTypeEnum(String value, String displayName, String help) {
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
