package pro.shushi.pamirs.middleware.schedule.eunmeration;

import pro.shushi.pamirs.middleware.schedule.directive.IntValueEnumeration;

import java.util.Calendar;
import java.util.Collection;

/**
 * time unit
 *
 * @author Adamancy Zhang
 * @date 2020-10-22 11:56
 */
public enum TimeUnit implements IntValueEnumeration<TimeUnit> {

    /**
     * 年
     *
     * @see Calendar#YEAR
     */
    YEAR(Calendar.YEAR),

    /**
     * 月
     *
     * @see Calendar#MONDAY
     */
    MONTH(Calendar.MONDAY),

    /**
     * 按年计算的日
     *
     * @see Calendar#DAY_OF_YEAR
     */
    DAY_OF_YEAR(Calendar.DAY_OF_YEAR),

    /**
     * 按月计算的日
     *
     * @see Calendar#DAY_OF_MONTH
     */
    DAY_OF_MONTH(Calendar.DAY_OF_MONTH),

    /**
     * 按周计算的日
     *
     * @see Calendar#DAY_OF_WEEK
     */
    DAY_OF_WEEK(Calendar.DAY_OF_WEEK),

    /**
     * 按当前月内的周计算的日
     *
     * @see Calendar#DAY_OF_WEEK_IN_MONTH
     */
    DAY_OF_WEEK_IN_MONTH(Calendar.DAY_OF_WEEK_IN_MONTH),

    /**
     * 按天计算的时
     *
     * @see Calendar#HOUR_OF_DAY
     */
    HOUR_OF_DAY(Calendar.HOUR_OF_DAY),

    /**
     * 分钟
     *
     * @see Calendar#MINUTE
     */
    MINUTE(Calendar.MINUTE),

    /**
     * 秒
     *
     * @see Calendar#SECOND
     */
    SECOND(Calendar.SECOND);

    private final int value;

    TimeUnit(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public Collection<TimeUnit> intValuesOf(int value) {
        throw new UnsupportedOperationException();
    }
}
