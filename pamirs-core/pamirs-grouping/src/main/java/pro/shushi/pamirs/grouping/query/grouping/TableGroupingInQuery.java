package pro.shushi.pamirs.grouping.query.grouping;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.entry.NullValue;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.GroupingDataWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.grouping.utils.TableGroupingDataHelper;
import pro.shushi.pamirs.grouping.utils.TableGroupingHelper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 可使用 IN 查询处理的表格分组查询
 *
 * @author Adamancy Zhang at 17:22 on 2025-11-14
 */
@Order(10)
@Component
public class TableGroupingInQuery<T> implements TableGroupingQueryApi<T> {

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        if (true) {
            return false;
        }
        return context.getQueryList().get(0).isSingleTableQuery();
    }

    @Override
    public void queryGroupingPage(TableGroupingQueryContext<T> context, TableGroupingResult result) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        TableGroupingFieldQuery firstQuery = queryList.get(0);
        Pagination<T> pagination = context.getPagination();
        List<T> list = TableGroupingHelper.queryFirstGroupingData(context, pagination);
        boolean isContainsNull = false;
        Map<String, GroupingDataWrapper> groupingDataMap = new LinkedHashMap<>();
        List<Object> inValues = new ArrayList<>();
        for (T data : list) {
            Object value = TableGroupingDataHelper.computeIfAbsent(groupingDataMap, firstQuery, data, false).getValue();
            if (NullValue.INSTANCE.equals(value)) {
                isContainsNull = true;
            } else {
                inValues.add(value);
            }
        }
        List<TableGroupingFieldQuery> memoryQueryList = new ArrayList<>();
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapper();
        for (TableGroupingFieldQuery query : queryList) {
            if (query.isSingleTableQuery()) {
                query.withSelect(queryWrapper);
                query.withGroupBy(queryWrapper);
            } else {
                memoryQueryList.add(query);
            }
        }
        if (firstQuery.isRelationOneField()) {
            List<String> relationColumns = firstQuery.getRelationColumns();
            List<String> referenceFields = firstQuery.getReferenceFields();
            List<List<Object>> collInValues = new ArrayList<>(referenceFields.size());
            for (Object inValue : inValues) {
                for (int i = 0; i < referenceFields.size(); i++) {
                    if (collInValues.size() < i + 1) {
                        collInValues.add(new ArrayList<>());
                    }
                    List<Object> newInValues = collInValues.get(i);
                    String referenceField = referenceFields.get(i);
                    newInValues.add(FieldUtils.getFieldValue(inValue, referenceField));
                }
            }
            queryWrapper.in(relationColumns, collInValues.toArray(new List[0]));
        } else {
            queryWrapper.in(firstQuery.getColumn(), inValues);
        }
        if (isContainsNull) {
            firstQuery.withNullWhere(queryWrapper);
        }
        List<T> others = Models.origin().queryListByWrapper(queryWrapper);
        if (!memoryQueryList.isEmpty()) {
            others = TableGroupingHelper.filter(others, memoryQueryList);
        }
        TableGroupingDataHelper.generatorGroupingDataList(groupingDataMap, queryList, others, false);
        result.setGroups(TableGroupingDataHelper.collectionGroupingData(context.getModel(), groupingDataMap, queryList));
        TableGroupingHelper.computePaging(pagination, result);
    }
}
