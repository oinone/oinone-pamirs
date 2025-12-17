package pro.shushi.pamirs.ux.grouping.statistic.defaults;

import pro.shushi.pamirs.ux.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticField;

import java.util.List;

/**
 * 统计唯一值数量占比
 *
 * @author Adamancy Zhang at 14:50 on 2025-11-20
 */
public class StatisticUniquePercent<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private final StatisticCount<T> total;

    private final StatisticUnique<T> uniqueCount;

    public StatisticUniquePercent(StatisticField statisticField) {
        super(statisticField);
        this.total = new StatisticCount<>(statisticField);
        this.uniqueCount = new StatisticUnique<>(statisticField);
    }

    @Override
    public void compute(List<T> data) {
        total.compute(data);
        uniqueCount.compute(data);
    }

    @Override
    protected void compute(T data, Object value) {
        // do nothing.
    }

    @Override
    public String getResult() {
        return getPercentStatisticValue(uniqueCount.getValue(), total.getValue());
    }
}
