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
 * 未填写
 *
 * @author Gesi at 9:39 on 2025/9/9
 */
@SPI.Service("NULL")
@Component
@Slf4j
public class GroupNullStatistic extends AbstractGroupStatisticApi implements GroupStatisticApi {

    @Override
    public <T> Object statistic(Grouping<T> group, GroupInfo<T> groupInfo, GroupField statisticField, List<T> dataList) {
        return null;
    }

}
