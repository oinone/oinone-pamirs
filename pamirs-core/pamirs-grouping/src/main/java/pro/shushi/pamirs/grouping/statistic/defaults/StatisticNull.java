package pro.shushi.pamirs.grouping.statistic.defaults;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticField;

import java.util.Collection;
import java.util.Map;

/**
 * 统计空值
 *
 * @author Adamancy Zhang at 14:50 on 2025-11-20
 */
public class StatisticNull<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private long total = 0;

    public StatisticNull(StatisticField statisticField) {
        super(statisticField);
    }

    @Override
    protected void compute(T data, Object value) {
        if (value == null
                || (value instanceof String && StringUtils.isBlank((String) value))
                || (value instanceof Collection<?> && CollectionUtils.isEmpty((Collection<?>) value))
                || (value instanceof Map<?, ?> && MapUtils.isEmpty((Map<?, ?>) value))) {
            total += 1;
        }
    }

    @Override
    public String getResult() {
        return String.valueOf(total);
    }

    public long getValue() {
        return total;
    }
}
