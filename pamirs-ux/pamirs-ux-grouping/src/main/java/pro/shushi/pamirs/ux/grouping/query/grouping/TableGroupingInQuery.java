package pro.shushi.pamirs.ux.grouping.query.grouping;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.entry.NullValue;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.ux.common.utils.QueryHelper;
import pro.shushi.pamirs.ux.grouping.entity.GroupingDataWrapper;
import pro.shushi.pamirs.ux.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.ux.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.ux.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.ux.grouping.utils.TableGroupingDataHelper;
import pro.shushi.pamirs.ux.grouping.utils.TableGroupingHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 可使用 IN 查询处理的表格分组查询
 *
 * @author Adamancy Zhang at 17:22 on 2025-11-14
 */
@Order(20)
@Component
public class TableGroupingInQuery<T> implements TableGroupingQueryApi<T> {

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        return context.getQueryList().get(0).isSingleTableQuery();
    }

    @Override
    public void queryGroupingPage(TableGroupingQueryContext<T> context, TableGroupingResult result) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        TableGroupingFieldQuery firstQuery = queryList.get(0);
        Pagination<T> pagination = context.getPagination();
        List<T> firstPageDataList = TableGroupingHelper.queryFirstGroupingData(context, pagination);
        boolean isContainsNull = false;
        Map<String, GroupingDataWrapper> groupingDataMap = new LinkedHashMap<>();
        List<Object> inValues = new ArrayList<>();
        for (T data : firstPageDataList) {
            Object value = TableGroupingDataHelper.computeIfAbsent(groupingDataMap, firstQuery, data, false).getValue();
            if (NullValue.INSTANCE.equals(value)) {
                isContainsNull = true;
            } else {
                inValues.add(value);
            }
        }
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapper();
        Consumer<QueryWrapper<T>> applyIn;
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
            applyIn = (w) -> w.in(relationColumns, collInValues.toArray(new List[0]));
        } else {
            applyIn = (w) -> w.in(firstQuery.getColumn(), inValues);
        }
        if (isContainsNull) {
            queryWrapper.and(w -> {
                applyIn.accept(w);
                firstQuery.withOrNullWhere(w);
            });
        } else {
            applyIn.accept(queryWrapper);
        }
        List<String> needQueryRelationFields = new ArrayList<>();
        for (TableGroupingFieldQuery query : queryList) {
            if (query.isSupportRelationQuery()) {
                needQueryRelationFields.add(query.getField());
            }
        }
        String model = context.getModel();
        if (needQueryRelationFields.isEmpty()) {
            QueryHelper.queryDataListByQueryPage(model, queryWrapper, (list) -> TableGroupingDataHelper.generatorGroupingDataList(groupingDataMap, queryList, list, false));
        } else {
            QueryHelper.queryDataListByQueryPage(model, queryWrapper, (list) -> {
                // FIXME: zbh 20251121 可按需查询关联关系字段
                for (String field : needQueryRelationFields) {
                    list = Models.origin().listFieldQuery(list, field);
                }
                TableGroupingDataHelper.generatorGroupingDataList(groupingDataMap, queryList, list, false);
            });
        }
        result.setGroups(TableGroupingDataHelper.collectionGroupingData(model, groupingDataMap, queryList));
        TableGroupingHelper.computePaging(pagination, result);
    }
}
