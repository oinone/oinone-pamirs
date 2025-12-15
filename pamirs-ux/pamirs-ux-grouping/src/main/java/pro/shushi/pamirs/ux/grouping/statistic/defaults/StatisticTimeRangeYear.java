package pro.shushi.pamirs.ux.grouping.statistic.defaults;

import pro.shushi.pamirs.ux.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticField;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticHelper;

import java.util.Date;

/**
 * 统计时间范围（日）
 *
 * @author Adamancy Zhang at 14:50 on 2025-11-20
 */
public class StatisticTimeRangeYear<T> extends StatisticTimeRangeMonth<T> implements StatisticApi<T> {

    public StatisticTimeRangeYear(StatisticField statisticField) {
        super(statisticField);
    }

    @Override
    protected long computeResult(Date max, Date min) {
        return StatisticHelper.timeRangeYear(max, min);
    }
}
