package pro.shushi.pamirs.grouping.query.grouping;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.entry.NullValue;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.GroupingDataWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.utils.TableGroupingDataHelper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

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
public class TableGroupingInQuery<T> extends AbstractTableGroupingQuery<T> implements TableGroupingQueryApi<T> {

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        TableGroupingFieldQuery firstQuery = context.getQueryList().get(0);
        if (firstQuery.isMulti()) {
            return firstQuery.isEnumField() && firstQuery.isBitDataDictionary();
        }
        return firstQuery.isBasicField() || firstQuery.isEnumField() || firstQuery.isRelationOneField();
    }

    @Override
    public void queryGroupingPage(TableGroupingQueryContext<T> context, TableGroupingResult result) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        TableGroupingFieldQuery firstQuery = queryList.get(0);
        Pagination<T> pagination = context.getPagination();
        List<T> list = queryFirstGroupingData(context, pagination);
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
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapper(firstQuery);
        generatorGroupsWrapper(queryWrapper, queryList);
        String column = firstQuery.getColumn();
        queryWrapper.in(column, inValues);
        if (isContainsNull) {
            if (firstQuery.isStringField()) {
                queryWrapper.or(w -> w.isNull(column).or().eq(column, CharacterConstants.SEPARATOR_EMPTY));
            } else {
                queryWrapper.or(w -> w.isNull(column));
            }
        }
        List<T> others = Models.origin().queryListByWrapper(queryWrapper);
        TableGroupingDataHelper.generatorGroupingDataList(groupingDataMap, queryList, others, false);
        result.setGroups(TableGroupingDataHelper.collectionGroupingData(context.getModel(), groupingDataMap, queryList));
        computePaging(pagination, result);
    }

    private void generatorGroupsWrapper(QueryWrapper<T> queryWrapper, List<TableGroupingFieldQuery> queryList) {
        List<String> selects = new ArrayList<>();
        List<String> groupBys = new ArrayList<>();
        for (TableGroupingFieldQuery query : queryList) {
            selects.add(query.getColumnAsField());
            groupBys.add(query.getColumn());
            query.withOrderBy(queryWrapper);
        }
        queryWrapper.select(selects.toArray(new String[0]));
        queryWrapper.groupBy(groupBys.toArray(new String[0]));
    }
}
