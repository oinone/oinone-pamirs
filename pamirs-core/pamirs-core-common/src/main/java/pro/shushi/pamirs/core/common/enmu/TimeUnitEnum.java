package pro.shushi.pamirs.core.common.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.util.Calendar;

@Dict(dictionary = TimeUnitEnum.dictionary, displayName = "日历时间单位")
public enum TimeUnitEnum implements IEnum<String> {

    YEAR("YEAR", "年", "年", Calendar.YEAR),
    MONTH("MONTH", "月", "月", Calendar.MONDAY),
    DAY_OF_YEAR("DAY_OF_YEAR", "日", "按年计算的日", Calendar.DAY_OF_YEAR),
    DAY_OF_MONTH("DAY_OF_MONTH", "日", "按月计算的日", Calendar.DAY_OF_MONTH),
    DAY_OF_WEEK("DAY_OF_WEEK", "日", "按周计算的日", Calendar.DAY_OF_WEEK),
    DAY_OF_WEEK_IN_MONTH("DAY_OF_WEEK_IN_MONTH", "日", "按当前月内的周计算的日", Calendar.DAY_OF_WEEK_IN_MONTH),
    HOUR_OF_DAY("HOUR", "时", "按天计算的时", Calendar.HOUR_OF_DAY),
    MINUTE("MINUTE", "分钟", "分钟", Calendar.MINUTE),
    SECOND("SECOND", "秒", "秒", Calendar.SECOND),
    ;

    public static final String dictionary = "resource.TimeUnitEnum";

    private String help;
    private String value;
    private String displayName;
    private Integer calendarValue;

    TimeUnitEnum(String value, String displayName, String help, Integer calendarValue) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
        this.calendarValue = calendarValue;
    }


    public String getHelp() {
        return help;
    }


    public String getValue() {
        return value;
    }


    public String getDisplayName() {
        return displayName;
    }

    public Integer getCalendarValue() {
        return calendarValue;
    }
}
