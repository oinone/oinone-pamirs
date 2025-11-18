package pro.shushi.pamirs.grouping.query.data;

import pro.shushi.pamirs.grouping.query.TableGroupingCommonQueryApi;
import pro.shushi.pamirs.grouping.query.grouping.TableGroupingQueryContext;

import java.util.List;

/**
 * 表格分组数据查询API
 *
 * @author Adamancy Zhang at 12:15 on 2025-11-17
 */
public interface TableGroupingDataQueryApi<T> extends TableGroupingCommonQueryApi<T> {

    List<T> queryGroupingDataByWrapper(TableGroupingQueryContext<T> context);

}
