package pro.shushi.pamirs.grouping.query.grouping;

import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.model.TableGroupingResult;

import java.util.List;

/**
 * X2M 关联字段分组查询
 *
 * @author Adamancy Zhang at 10:59 on 2025-11-17
 */
//@Order(0)
//@Component
public class TableGroupingRelationManyQuery<T> extends AbstractTableGroupingQuery<T> implements TableGroupingQueryApi<T> {

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        for (TableGroupingFieldQuery query : queryList) {
            if (query.isRelationManyField()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void queryGroupingPage(TableGroupingQueryContext<T> context, TableGroupingResult result) {

    }
}
