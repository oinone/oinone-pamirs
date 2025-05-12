package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 字段数据管理策略枚举
 * <p>
 * DML strategy
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FieldStrategy", displayName = "字段数据管理策略")
public enum FieldStrategyEnum implements IEnum<String> {

    IGNORED("ignore", "忽略", "忽略"),
    NOT_NULL("not_null", "非空", "非空"),
    NOT_EMPTY("not_empty", "非空字符串", "非空字符串"),
    DEFAULT("default", "默认", "默认，是否设置值"),
    NOT_CHANGE("not_change", "未变更", "未变更"),
    NEVER("never", "永不", "永不");

    private final String help;
    private final String value;
    private final String displayName;

    FieldStrategyEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
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
