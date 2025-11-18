package pro.shushi.pamirs.grouping.query.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.grouping.query.data.TableGroupingDataUniversalQuery;
import pro.shushi.pamirs.grouping.query.grouping.TableGroupingQueryContext;

import java.util.List;

/**
 * 万能的表格数据统计查询查询
 *
 * @author Adamancy Zhang at 09:41 on 2025-11-18
 */
@Order(999)
@Component
public class TableGroupingStatisticUniversalQuery<T> implements TableGroupingStatisticQueryApi<T> {

    @Autowired
    private TableGroupingDataUniversalQuery<T> tableGroupingDataUniversalQuery;

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        return true;
    }

    @Override
    public String queryGroupingStatistic(TableGroupingQueryContext<T> context) {
        List<T> list = tableGroupingDataUniversalQuery.queryGroupingDataByWrapper(context);
        // FIXME: zbh 20251118 内存统计获取结果
        return null;
    }
}
