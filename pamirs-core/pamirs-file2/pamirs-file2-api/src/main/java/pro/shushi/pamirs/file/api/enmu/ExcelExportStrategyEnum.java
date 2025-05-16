package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 导出策略
 *
 * @author Adamancy Zhang at 13:31 on 2024-03-28
 */
@Base
@Dict(dictionary = ExcelExportStrategyEnum.DICTIONARY, displayName = "导出策略", summary = "导出策略")
public enum ExcelExportStrategyEnum implements IEnum<String> {

    STANDARD(ExcelExportStrategyEnum.standard, "默认获取", "默认使用单个扩展点获取整个工作簿的全部数据"),
    // SINGLE(ExcelExportStrategyEnum.single, "单一函数获取", "使用工作簿中定义的函数完整获取整个工作簿的全部数据"),
    BLOCK(ExcelExportStrategyEnum.block, "多函数获取", "使用块定义的函数分别获取块中的数据"),
    STREAM(ExcelExportStrategyEnum.stream, "流式获取", "通过分页获取每个块中的数据，获取数据与填充数据交替执行"),
    ;

    public static final String DICTIONARY = "file.ExcelExportStrategyEnum";

    public static final String standard = "STANDARD";

//    public static final String single = "SINGLE";

    public static final String block = "BLOCK";

    public static final String stream = "STREAM";

    private final String value;
    private final String displayName;
    private final String help;

    ExcelExportStrategyEnum(String value, String displayName, String help) {
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
