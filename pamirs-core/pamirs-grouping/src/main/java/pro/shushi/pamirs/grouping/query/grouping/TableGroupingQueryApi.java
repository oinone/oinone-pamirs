package pro.shushi.pamirs.grouping.query.grouping;

import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.query.TableGroupingCommonQueryApi;

/**
 * 表格分组查询API
 *
 * @author Adamancy Zhang at 16:48 on 2025-11-14
 */
public interface TableGroupingQueryApi<T> extends TableGroupingCommonQueryApi<T> {

    void queryGroupingPage(TableGroupingQueryContext<T> context, TableGroupingResult result);

}
