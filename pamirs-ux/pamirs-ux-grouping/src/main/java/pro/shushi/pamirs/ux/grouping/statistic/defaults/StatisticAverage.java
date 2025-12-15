package pro.shushi.pamirs.ux.grouping.statistic.defaults;

import pro.shushi.pamirs.ux.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticField;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 统计平均值
 *
 * @author Adamancy Zhang at 15:51 on 2025-11-20
 */
public class StatisticAverage<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private final StatisticCount<T> total;

    private final StatisticSum<T> sum;

    public StatisticAverage(StatisticField statisticField) {
        super(statisticField);
        this.total = new StatisticCount<>(statisticField);
        this.sum = new StatisticSum<>(statisticField);
    }

    @Override
    public void compute(List<T> data) {
        total.compute(data);
        sum.compute(data);
    }

    @Override
    protected void compute(T data, Object value) {
        // do nothing.
    }

    @Override
    public String getResult() {
        BigDecimal sumValue = sum.getValue();
        if (sumValue == null) {
            return getInvalidStatisticValue();
        }
        return getNumberStatisticValue(sumValue.divide(BigDecimal.valueOf(total.getValue()), 2, RoundingMode.HALF_UP));
    }
}
