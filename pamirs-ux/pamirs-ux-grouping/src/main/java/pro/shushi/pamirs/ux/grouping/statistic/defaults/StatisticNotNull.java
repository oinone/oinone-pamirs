package pro.shushi.pamirs.ux.grouping.statistic.defaults;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.ux.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticField;

import java.util.Collection;
import java.util.Map;

/**
 * 统计非空值
 *
 * @author Adamancy Zhang at 14:50 on 2025-11-20
 */
public class StatisticNotNull<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private long total = 0;

    public StatisticNotNull(StatisticField statisticField) {
        super(statisticField);
    }

    @Override
    protected void compute(T data, Object value) {
        if (value != null) {
            if (value instanceof String) {
                if (StringUtils.isNotBlank((String) value)) {
                    total += 1;
                }
            } else if (value instanceof Collection) {
                if (CollectionUtils.isNotEmpty((Collection<?>) value)) {
                    total += 1;
                }
            } else if (value instanceof Map) {
                if (MapUtils.isNotEmpty((Map<?, ?>) value)) {
                    total += 1;
                }
            } else {
                total += 1;
            }
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
