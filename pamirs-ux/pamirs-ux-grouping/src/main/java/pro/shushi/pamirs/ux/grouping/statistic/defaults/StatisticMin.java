package pro.shushi.pamirs.ux.grouping.statistic.defaults;

import pro.shushi.pamirs.ux.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticField;
import pro.shushi.pamirs.ux.common.utils.NumberHelper;

import java.math.BigDecimal;

/**
 * 统计最小值
 *
 * @author Adamancy Zhang at 15:51 on 2025-11-20
 */
public class StatisticMin<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private BigDecimal min;

    public StatisticMin(StatisticField statisticField) {
        super(statisticField);
    }

    @Override
    protected void compute(T data, Object value) {
        BigDecimal numberValue = NumberHelper.valueOfNullable(value);
        if (numberValue == null) {
            return;
        }
        if (min == null) {
            min = numberValue;
        } else if (min.compareTo(numberValue) > 0) {
            min = numberValue;
        }
    }

    @Override
    public String getResult() {
        return getNumberStatisticValue(min);
    }
}
