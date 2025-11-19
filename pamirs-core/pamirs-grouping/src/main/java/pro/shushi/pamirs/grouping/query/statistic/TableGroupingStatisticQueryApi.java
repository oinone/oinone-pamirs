package pro.shushi.pamirs.grouping.query.statistic;

import pro.shushi.pamirs.grouping.query.TableGroupingCommonQueryApi;
import pro.shushi.pamirs.grouping.query.TableGroupingQueryContext;

/**
 * 表格分组统计查询API
 *
 * @author Adamancy Zhang at 12:17 on 2025-11-17
 */
public interface TableGroupingStatisticQueryApi<T> extends TableGroupingCommonQueryApi<T> {

    String queryGroupingStatistic(TableGroupingQueryContext<T> context);

}
