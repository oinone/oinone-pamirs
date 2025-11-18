package pro.shushi.pamirs.grouping.statistic;

import pro.shushi.pamirs.grouping.model.GroupingData;
import pro.shushi.pamirs.grouping.model.TableGroupingWrapper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

/**
 * 分组统计函数计算
 *
 * @author Gesi at 14:02 on 2025/9/5
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface GroupStatisticApi {

    Object statistic(TableGroupingWrapper group, GroupingData groupInfo, String statisticField, List<?> dataList);

}
