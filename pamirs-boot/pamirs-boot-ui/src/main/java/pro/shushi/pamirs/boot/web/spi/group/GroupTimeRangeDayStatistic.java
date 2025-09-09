package pro.shushi.pamirs.boot.web.spi.group;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.tmodel.GroupInfo;
import pro.shushi.pamirs.boot.base.tmodel.Grouping;
import pro.shushi.pamirs.boot.web.spi.api.GroupStatisticApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;
import java.util.Map;

/**
 * 时间范围（日）
 *
 * @author Gesi at 9:39 on 2025/9/9
 */
@SPI.Service("TIME_RANGE_DAY")
@Component
@Slf4j
public class GroupTimeRangeDayStatistic extends AbstractGroupStatisticApi implements GroupStatisticApi {

    @Override
    public <T> Map<String, Object> statistic(Grouping<T> group, GroupInfo<T> groupInfo, List<T> dataList) {
        return null;
    }

}
