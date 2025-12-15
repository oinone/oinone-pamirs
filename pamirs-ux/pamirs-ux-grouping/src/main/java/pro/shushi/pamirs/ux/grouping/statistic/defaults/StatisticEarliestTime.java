package pro.shushi.pamirs.ux.grouping.statistic.defaults;

import pro.shushi.pamirs.ux.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticField;
import pro.shushi.pamirs.meta.util.DateUtils;

import java.util.Date;

/**
 * 统计最早时间
 *
 * @author Adamancy Zhang at 15:51 on 2025-11-20
 */
public class StatisticEarliestTime<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private Date min;

    public StatisticEarliestTime(StatisticField statisticField) {
        super(statisticField);
    }

    @Override
    protected void compute(T data, Object value) {
        Date dateValue = DateUtils.toDate(value);
        if (dateValue == null) {
            return;
        }
        if (min == null) {
            min = dateValue;
        } else if (min.after(dateValue)) {
            min = dateValue;
        }
    }

    @Override
    public String getResult() {
        return getDateStatisticValue(min);
    }

    public Date getValue() {
        return min;
    }
}
