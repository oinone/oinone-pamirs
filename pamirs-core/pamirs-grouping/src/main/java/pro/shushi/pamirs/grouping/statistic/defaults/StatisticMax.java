package pro.shushi.pamirs.grouping.statistic.defaults;

import pro.shushi.pamirs.core.common.NumberHelper;
import pro.shushi.pamirs.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticField;

import java.math.BigDecimal;

/**
 * 统计最大值
 *
 * @author Adamancy Zhang at 15:51 on 2025-11-20
 */
public class StatisticMax<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private BigDecimal max;

    public StatisticMax(StatisticField statisticField) {
        super(statisticField);
    }

    @Override
    protected void compute(T data, Object value) {
        BigDecimal numberValue = NumberHelper.valueOfNullable(value);
        if (numberValue == null) {
            return;
        }
        if (max == null) {
            max = numberValue;
        } else if (max.compareTo(numberValue) < 0) {
            max = numberValue;
        }
    }

    @Override
    public String getResult() {
        return getNumberStatisticValue(max);
    }
}
