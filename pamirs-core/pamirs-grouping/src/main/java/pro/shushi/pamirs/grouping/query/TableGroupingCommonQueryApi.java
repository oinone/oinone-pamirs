package pro.shushi.pamirs.grouping.query;

import pro.shushi.pamirs.grouping.query.grouping.TableGroupingQueryContext;

/**
 * 表格分组公共查询API
 *
 * @author Adamancy Zhang at 12:21 on 2025-11-17
 */
public interface TableGroupingCommonQueryApi<T> {

    boolean match(TableGroupingQueryContext<T> context);

}
