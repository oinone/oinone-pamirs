package pro.shushi.pamirs.grouping.statistic;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.grouping.model.GroupInfo;
import pro.shushi.pamirs.grouping.model.Grouping;
import pro.shushi.pamirs.boot.web.utils.GroupStatisticUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Date;
import java.util.List;

/**
 * 时间范围（年）
 *
 * @author Gesi at 9:39 on 2025/9/9
 */
@SPI.Service("TIME_RANGE_YEAR")
@Component
@Slf4j
public class GroupTimeRangeYearStatistic extends AbstractGroupStatisticApi implements GroupStatisticApi {

    @Override
    public <T> Object statistic(Grouping<T> group, GroupInfo<T> groupInfo, String statisticField, List<?> dataList) {
        Pair<Date, Date> dateRange = earliestTimeAndLatestTime(dataList);
        return GroupStatisticUtils.timeRangeYear(dateRange.getLeft(), dateRange.getRight());
    }

}
