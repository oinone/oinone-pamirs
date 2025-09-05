package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author Gesi at 15:50 on 2025/9/1
 */
@Base
@Dict(dictionary = GroupStatisticTypeEnum.DICTIONARY, displayName = "统计类型")
public enum GroupStatisticTypeEnum implements IEnum<String> {

    NONE("NONE", "不展示", "不展示"),
    COUNT("COUNT", "记录总数", "记录总数"),
    NULL("NULL", "未填写", "未填写"),
    NOT_NULL("NOT_NULL", "已填写", "已填写"),
    NULL_PERCENT("NULL_PERCENT", "未填写占比", "未填写占比"),
    NOT_NULL_PERCENT("NOT_NULL_PERCENT", "已填写占比", "已填写占比"),
    EARLIEST_TIME("NOT_NULL_PERCENT", "最早时间", "最早时间"),
    LATEST_TIME("NOT_NULL_PERCENT", "最晚时间", "最晚时间"),
    TIME_RANGE_DAY("NOT_NULL_PERCENT", "时间范围（日）", "时间范围（日）"),
    TIME_RANGE_MONTH("NOT_NULL_PERCENT", "时间范围（月）", "时间范围（月）"),
    TIME_RANGE_YEAR("NOT_NULL_PERCENT", "时间范围（年）", "时间范围（年）"),
    SUM("NOT_NULL_PERCENT", "求和", "求和"),
    AVERAGE("NOT_NULL_PERCENT", "平均值", "平均值"),
    MEDIAN("NOT_NULL_PERCENT", "中位数", "中位数"),
    MAX("NOT_NULL_PERCENT", "最大值", "最大值"),
    MIN("NOT_NULL_PERCENT", "最小值", "最小值"),
    ;

    public static final String DICTIONARY = "base.GroupStatisticTypeEnum";

    private final String value;
    private final String displayName;
    private final String help;

    GroupStatisticTypeEnum(String value, String displayName, String help) {
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
