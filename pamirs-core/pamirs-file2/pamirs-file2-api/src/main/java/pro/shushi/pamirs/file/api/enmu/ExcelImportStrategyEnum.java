package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 导入策略
 *
 * @author Adamancy Zhang at 13:23 on 2024-03-28
 */
@Base
@Dict(dictionary = ExcelImportStrategyEnum.DICTIONARY, displayName = "导入策略", summary = "导入策略")
public enum ExcelImportStrategyEnum implements IEnum<String> {

    STANDARD("STANDARD", "标准模式", "标准模式（默认），开发人员自行控制所有导入过程"),
    EACH("EACH", "逐行导入", "自动收集导入错误生成错误文件"),
    ALL("ALL", "全部成功", "自动开启导入事务，出现错误自动中断"),
    ALL_EACH("ALL", "全部成功并收集错误", "自动开启导入事务，自动收集导入错误生成错误文件"),
    ;

    public static final String DICTIONARY = "file.ExcelImportStrategyEnum";

    private final String value;
    private final String displayName;
    private final String help;

    ExcelImportStrategyEnum(String value, String displayName, String help) {
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
