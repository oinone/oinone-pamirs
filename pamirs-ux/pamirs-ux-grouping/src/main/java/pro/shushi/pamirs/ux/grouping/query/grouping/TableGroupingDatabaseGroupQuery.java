package pro.shushi.pamirs.ux.grouping.query.grouping;

import pro.shushi.pamirs.ux.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.ux.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.ux.grouping.query.TableGroupingQueryContext;

import java.util.List;

/**
 * 单表数据库分组
 *
 * @author Adamancy Zhang at 22:03 on 2025-11-20
 */
//@Order(30)
//@Component
public class TableGroupingDatabaseGroupQuery<T> implements TableGroupingQueryApi<T> {

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        if (true) {
            return false;
        }
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        for (TableGroupingFieldQuery query : queryList) {
            if (!query.isSingleTableQuery()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void queryGroupingPage(TableGroupingQueryContext<T> context, TableGroupingResult result) {

    }
}
