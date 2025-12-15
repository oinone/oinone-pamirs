package pro.shushi.pamirs.ux.grouping.statistic.defaults;

import pro.shushi.pamirs.ux.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticField;
import pro.shushi.pamirs.meta.util.DateUtils;

import java.util.Date;

/**
 * 统计最晚时间
 *
 * @author Adamancy Zhang at 15:51 on 2025-11-20
 */
public class StatisticLatestTime<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private Date max;

    public StatisticLatestTime(StatisticField statisticField) {
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
    }

    @Override
    public String getResult() {
        return getDateStatisticValue(max);
    }

    public Date getValue() {
        return max;
    }
}
