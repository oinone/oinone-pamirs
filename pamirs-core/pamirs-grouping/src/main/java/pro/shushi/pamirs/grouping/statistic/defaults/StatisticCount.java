package pro.shushi.pamirs.grouping.statistic.defaults;

import pro.shushi.pamirs.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticField;

import java.util.List;

/**
 * 统计数量
 *
 * @author Adamancy Zhang at 14:51 on 2025-11-20
 */
public class StatisticCount<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private long total = 0;

    public StatisticCount(StatisticField statisticField) {
        super(statisticField);
    }

    @Override
    public void compute(List<T> data) {
        total += data.size();
    }

    @Override
    protected void compute(T data, Object value) {
        // do nothing.
    }

    @Override
    public String getResult() {
        return String.valueOf(total);
    }

    public long getValue() {
        return total;
    }
}
