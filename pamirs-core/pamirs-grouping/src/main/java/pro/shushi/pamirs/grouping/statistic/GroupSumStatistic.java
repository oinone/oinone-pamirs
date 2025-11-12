package pro.shushi.pamirs.grouping.statistic;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.grouping.model.GroupInfo;
import pro.shushi.pamirs.grouping.model.Grouping;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.math.BigDecimal;
import java.util.List;

/**
 * 求和
 *
 * @author Gesi at 9:39 on 2025/9/9
 */
@SPI.Service("SUM")
@Component
@Slf4j
public class GroupSumStatistic extends AbstractGroupStatisticApi implements GroupStatisticApi {

    @Override
    public <T> Object statistic(Grouping<T> group, GroupInfo<T> groupInfo, String statisticField, List<?> dataList) {
        List<BigDecimal> numberList = formatNumber(dataList);
        return sum(numberList);
    }

}
