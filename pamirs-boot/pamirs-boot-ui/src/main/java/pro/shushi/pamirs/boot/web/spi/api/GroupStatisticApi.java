package pro.shushi.pamirs.boot.web.spi.api;

import pro.shushi.pamirs.boot.base.tmodel.GroupInfo;
import pro.shushi.pamirs.boot.base.tmodel.Grouping;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;
import java.util.Map;

/**
 * 分组统计函数计算
 *
 * @author Gesi at 14:02 on 2025/9/5
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface GroupStatisticApi {

    <T> Map<String, Object> statistic(Grouping<T> group, GroupInfo<T> groupInfo, List<T> dataList);

}
