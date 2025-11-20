package pro.shushi.pamirs.grouping.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author Gesi at 15:50 on 2025/9/1
 */
@Base
@Dict(dictionary = GroupStatisticMethodEnum.DICTIONARY, displayName = "统计方式")
public enum GroupStatisticMethodEnum implements IEnum<String> {

    NONE("NONE", "不展示", "不展示"),
    COUNT("COUNT", "记录总数", "记录总数"),
    NULL("NULL", "未填写", "未填写"),
    NOT_NULL("NOT_NULL", "已填写", "已填写"),
    NULL_PERCENT("NULL_PERCENT", "未填写占比", "未填写占比"),
    NOT_NULL_PERCENT("NOT_NULL_PERCENT", "已填写占比", "已填写占比"),
    MAX("MAX", "最大值", "最大值"),
    MIN("MIN", "最小值", "最小值"),
    UNIQUE("UNIQUE", "唯一值", "唯一值"),
    UNIQUE_PERCENT("UNIQUE_PERCENT", "唯一值占比", "唯一值占比"),
    EARLIEST_TIME("EARLIEST_TIME", "最早时间", "最早时间"),
    LATEST_TIME("LATEST_TIME", "最晚时间", "最晚时间"),
    TIME_RANGE_DAY("TIME_RANGE_DAY", "时间范围（日）", "时间范围（日）"),
    TIME_RANGE_MONTH("TIME_RANGE_MONTH", "时间范围（月）", "时间范围（月）"),
    TIME_RANGE_YEAR("TIME_RANGE_YEAR", "时间范围（年）", "时间范围（年）"),
    SUM("SUM", "求和", "求和"),
    AVERAGE("AVERAGE", "平均值", "平均值"),
    MEDIAN("MEDIAN", "中位数", "中位数"),
    ;

    public static final String DICTIONARY = "grouping.GroupStatisticMethodEnum";

    private final String value;
    private final String displayName;
    private final String help;

    GroupStatisticMethodEnum(String value, String displayName, String help) {
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

    public static GroupStatisticMethodEnum valueOfNullable(String value) {
        for (GroupStatisticMethodEnum item : GroupStatisticMethodEnum.values()) {
            if (item.value().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
