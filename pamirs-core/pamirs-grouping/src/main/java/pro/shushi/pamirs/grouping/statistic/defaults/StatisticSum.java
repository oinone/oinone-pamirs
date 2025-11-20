package pro.shushi.pamirs.grouping.statistic.defaults;

import pro.shushi.pamirs.core.common.NumberHelper;
import pro.shushi.pamirs.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticField;

import java.math.BigDecimal;

/**
 * 统计合计
 *
 * @author Adamancy Zhang at 15:51 on 2025-11-20
 */
public class StatisticSum<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private BigDecimal sum;

    public StatisticSum(StatisticField statisticField) {
        super(statisticField);
    }

    @Override
    protected void compute(T data, Object value) {
        BigDecimal numberValue = NumberHelper.valueOfNullable(value);
        if (numberValue == null) {
            return;
        }
        if (sum == null) {
            sum = numberValue;
        } else {
            sum = sum.add(numberValue);
        }
    }

    @Override
    public String getResult() {
        return getNumberStatisticValue(sum);
    }

    public BigDecimal getValue() {
        return sum;
    }
}
