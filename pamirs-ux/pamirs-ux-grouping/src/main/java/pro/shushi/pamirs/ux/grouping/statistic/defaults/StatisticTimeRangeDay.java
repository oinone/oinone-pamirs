package pro.shushi.pamirs.ux.grouping.statistic.defaults;

import pro.shushi.pamirs.ux.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticField;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticHelper;
import pro.shushi.pamirs.meta.util.DateUtils;

import java.util.Date;

/**
 * 统计时间范围（日）
 *
 * @author Adamancy Zhang at 14:50 on 2025-11-20
 */
public class StatisticTimeRangeDay<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private Date max;

    private Date min;

    public StatisticTimeRangeDay(StatisticField statisticField) {
        super(statisticField);
    }

    @Override
    protected void compute(T data, Object value) {
        Date dateValue = DateUtils.toDate(value);
        if (dateValue == null) {
            return;
        }
        if (max == null) {
            max = dateValue;
        } else if (max.before(dateValue)) {
            max = dateValue;
        }
        if (min == null) {
            min = dateValue;
        } else if (min.after(dateValue)) {
            min = dateValue;
        }
    }

    @Override
    public String getResult() {
        if (max == null) {
            return getInvalidStatisticValue();
        }
        return String.valueOf(computeResult(max, min));
    }

    public Date getMax() {
        return max;
    }

    public Date getMin() {
        return min;
    }

    protected long computeResult(Date max, Date min) {
        return StatisticHelper.timeRangeDay(max, min);
    }
}
