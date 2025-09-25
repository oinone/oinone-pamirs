package pro.shushi.pamirs.boot.web.spi.group;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.tmodel.GroupInfo;
import pro.shushi.pamirs.boot.base.tmodel.Grouping;
import pro.shushi.pamirs.boot.web.spi.api.GroupStatisticApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;

/**
 * 唯一值占比
 *
 * @author Gesi at 9:39 on 2025/9/9
 */
@SPI.Service("UNIQUE_PERCENT")
@Component
@Slf4j
public class GroupUniquePercentStatistic extends AbstractGroupStatisticApi implements GroupStatisticApi {

    @Override
    public <T> Object statistic(Grouping<T> group, GroupInfo<T> groupInfo, String statisticField, List<?> dataList) {
        long total = total(dataList);
        if (total == 0) {
            return 0;
        }
        return String.format("%.2f", ((double) unique(dataList)) / total * 100);
    }

}
