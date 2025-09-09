package pro.shushi.pamirs.boot.web.spi.group;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.tmodel.GroupField;
import pro.shushi.pamirs.boot.base.tmodel.GroupInfo;
import pro.shushi.pamirs.boot.base.tmodel.Grouping;
import pro.shushi.pamirs.boot.web.spi.api.GroupStatisticApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;

/**
 * 时间范围（月）
 *
 * @author Gesi at 9:39 on 2025/9/9
 */
@SPI.Service("TIME_RANGE_MONTH")
@Component
@Slf4j
public class GroupTimeRangeMonthStatistic extends AbstractGroupStatisticApi implements GroupStatisticApi {

    @Override
    public <T> Object statistic(Grouping<T> group, GroupInfo<T> groupInfo, GroupField statisticField, List<?> dataList) {
        return "mock TIME_RANGE_MONTH";
    }

}
