package pro.shushi.pamirs.boot.web.spi.group;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.tmodel.GroupInfo;
import pro.shushi.pamirs.boot.base.tmodel.Grouping;
import pro.shushi.pamirs.boot.web.spi.api.GroupStatisticApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.math.BigDecimal;
import java.util.List;

/**
 * 最大值
 *
 * @author Gesi at 9:39 on 2025/9/9
 */
@SPI.Service("MAX")
@Component
@Slf4j
public class GroupMaxStatistic extends AbstractGroupStatisticApi implements GroupStatisticApi {

    @Override
    public <T> Object statistic(Grouping<T> group, GroupInfo<T> groupInfo, String statisticField, List<?> dataList) {
        List<BigDecimal> numberList = formatNumber(dataList);
        return max(numberList);
    }

}
