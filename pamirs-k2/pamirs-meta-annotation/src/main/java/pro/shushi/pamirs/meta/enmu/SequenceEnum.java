package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 序列生成方式枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.Sequence", displayName = "序列生成方式")
public enum SequenceEnum implements IEnum<String> {

    SEQ("SEQ", "SEQ", "自增流水号"),
    ORDERLY_SEQ("ORDERLY_SEQ", "ORDERLY_SEQ", "自增强有序流水号"),
    DATE_SEQ("DATE_SEQ", "DATE_SEQ", "日期流水号"),
    DATE_ORDERLY_SEQ("DATE_ORDERLY_SEQ", "DATE_ORDERLY_SEQ", "日期强有序流水号"),
    DATE("DATE", "DATE", "日期"),
    UUID("UUID", "UUID", "UUID"),
    DISTRIBUTION("DISTRIBUTION", "分布式ID", "分布式ID"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    SequenceEnum(String value, String displayName, String help) {
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

    public static SequenceEnum valueOfNullable(String sequence) {
        for (SequenceEnum item : SequenceEnum.values()) {
            if (item.value.equals(sequence)) {
                return item;
            }
        }
        return null;
    }
}
