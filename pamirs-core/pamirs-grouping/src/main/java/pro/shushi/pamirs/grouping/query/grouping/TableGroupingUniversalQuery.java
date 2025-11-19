package pro.shushi.pamirs.grouping.query.grouping;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.grouping.utils.TableGroupingHelper;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * 万能的表格分组查询
 *
 * @author Adamancy Zhang at 17:07 on 2025-11-14
 */
@Order(999)
@Component
public class TableGroupingUniversalQuery<T> implements TableGroupingQueryApi<T> {

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        return true;
    }

    @Override
    public void queryGroupingPage(TableGroupingQueryContext<T> context, TableGroupingResult result) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        Pagination<T> pagination = context.getPagination();
        List<T> list = TableGroupingHelper.fetchGroupingDataList(queryList, context.generatorQueryWrapperWithOrderBy(), false);
        if (CollectionUtils.isEmpty(list)) {
            result.setGroups(new ArrayList<>());
            TableGroupingHelper.computePaging(pagination, result);
            return;
        }
        result.setGroups(TableGroupingHelper.fullDataConvertGroups(queryList, context.getModel(), list));
        TableGroupingHelper.computePaging(pagination, result);
    }
}
