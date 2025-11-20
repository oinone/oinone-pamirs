package pro.shushi.pamirs.grouping.statistic.defaults;

import pro.shushi.pamirs.core.common.NumberHelper;
import pro.shushi.pamirs.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticField;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统计中位数
 *
 * @author Adamancy Zhang at 15:51 on 2025-11-20
 */
public class StatisticMedian<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private final List<BigDecimal> validValues = new ArrayList<>();

    public StatisticMedian(StatisticField statisticField) {
        super(statisticField);
    }

    @Override
    protected void compute(T data, Object value) {
        validValues.add(NumberHelper.valueOf(value));
    }

    @Override
    public String getResult() {
        List<BigDecimal> results = validValues.stream().sorted().collect(Collectors.toList());
        int size = results.size();
        if (size == 0) {
            return getInvalidStatisticValue();
        }
        if (size == 1) {
            return getNumberStatisticValue(results.get(0));
        }
        int mid = size / 2;
        if (size % 2 == 0) {
            BigDecimal a = results.get(mid - 1);
            BigDecimal b = results.get(mid);
            return getNumberStatisticValue(a.add(b).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP));
        } else {
            return getNumberStatisticValue(results.get(mid));
        }
    }
}
