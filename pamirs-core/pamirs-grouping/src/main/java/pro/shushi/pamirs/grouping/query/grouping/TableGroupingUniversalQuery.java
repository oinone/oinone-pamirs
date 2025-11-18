package pro.shushi.pamirs.grouping.query.grouping;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.GroupingDataWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.utils.TableGroupingDataHelper;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 万能的表格分组查询
 *
 * @author Adamancy Zhang at 17:07 on 2025-11-14
 */
@Order(999)
@Component
public class TableGroupingUniversalQuery<T> extends AbstractTableGroupingQuery<T> implements TableGroupingQueryApi<T> {

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        return true;
    }

    @Override
    public void queryGroupingPage(TableGroupingQueryContext<T> context, TableGroupingResult result) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        Pagination<T> pagination = context.getPagination();
        Map<String, GroupingDataWrapper> groupingDataMap = queryFirstGroupingDataMap(context, pagination, false);
        Map<String, GroupingDataWrapper> lastGroupingData = groupingDataMap;
        int lastIndex = queryList.size() - 1;
        for (int i = 1; i < lastIndex; i++) {
            TableGroupingFieldQuery query = queryList.get(i);
            List<GroupingDataWrapper> groupingDataList = queryGroupingDataWrapper(context, query, false);
            lastGroupingData = TableGroupingDataHelper.mergeGroupingDataList(lastGroupingData, groupingDataList);
        }
        List<GroupingDataWrapper> groupingDataList = queryGroupingDataWrapper(context, queryList.get(lastIndex), true);
        TableGroupingDataHelper.mergeGroupingDataList(lastGroupingData, groupingDataList);
        result.setGroups(TableGroupingDataHelper.collectionGroupingData(context.getModel(), groupingDataMap, queryList));
        computePaging(pagination, result);
    }

    private List<GroupingDataWrapper> queryGroupingDataWrapper(TableGroupingQueryContext<T> context, TableGroupingFieldQuery query, Boolean isLeaf) {
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapperWithGroupBy(query);
        return queryPageAll(queryWrapper, (dataList) -> {
            List<GroupingDataWrapper> sublist = new ArrayList<>();
            for (T data : dataList) {
                GroupingDataWrapper groupingDataWrapper = TableGroupingDataHelper.generatorGroupingDataWrapper(query, data, isLeaf);
                TableGroupingFieldQuery parent = query.getParent();
                if (parent != null) {
                    groupingDataWrapper.setParentKey(TableGroupingDataHelper.getGroupKeyByData(parent, data));
                    groupingDataWrapper.setParentField(parent.getField());
                }
                sublist.add(groupingDataWrapper);
            }
            return sublist;
        });
    }
}
