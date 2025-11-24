package pro.shushi.pamirs.grouping.query.grouping;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.grouping.utils.TableGroupingDataHelper;
import pro.shushi.pamirs.grouping.utils.TableGroupingHelper;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 仅有一个分组
 *
 * @author Adamancy Zhang at 21:59 on 2025-11-20
 */
@Order(10)
@Component
public class TableGroupingSingleGroupQuery<T> implements TableGroupingQueryApi<T> {

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        return context.getQueryList().size() == 1;
    }

    @Override
    public void queryGroupingPage(TableGroupingQueryContext<T> context, TableGroupingResult result) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        Pagination<T> pagination = context.getPagination();
        TableGroupingFieldQuery firstQuery = queryList.get(0);
        if (firstQuery.isSingleTableQuery() || (firstQuery.isRelationManyField() && !context.getQueryStrategy().isRelationManyShowNull())) {
            result.setGroups(TableGroupingDataHelper.collectionGroupingData(context.getModel(), TableGroupingHelper.queryFirstGroupingDataMap(context, pagination, true), queryList));
            TableGroupingHelper.computePaging(pagination, result);
        } else {
            result.setGroups(TableGroupingHelper.fetchGroupingDataList(context.getGroupingModel(), queryList, context.generatorQueryWrapper()));
            TableGroupingHelper.memoryPaging(pagination, result);
        }
    }
}
