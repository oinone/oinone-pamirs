package pro.shushi.pamirs.ux.grouping.query.grouping;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.ux.grouping.entity.GroupingDataWrapper;
import pro.shushi.pamirs.ux.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.ux.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.ux.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.ux.grouping.utils.TableGroupingDataHelper;
import pro.shushi.pamirs.ux.grouping.utils.TableGroupingHelper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 表格分组层级查询
 *
 * @author Adamancy Zhang at 11:05 on 2025-11-24
 */
//@Order(40)
//@Component
public class TableGroupingLevelQuery<T> implements TableGroupingQueryApi<T> {

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        if (true) {
            return false;
        }
        return !context.getQueryStrategy().isRelationManyShowNull();
    }

    @Override
    public void queryGroupingPage(TableGroupingQueryContext<T> context, TableGroupingResult result) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        Pagination<T> pagination = context.getPagination();
        Map<String, GroupingDataWrapper> groupingDataMap = TableGroupingHelper.queryFirstGroupingDataMap(context, pagination, false);
        Map<String, GroupingDataWrapper> lastGroupingData = groupingDataMap;
        TableGroupingFieldQuery parentQuery = queryList.get(0);
        int lastIndex = queryList.size() - 1;
        for (int i = 1; i < queryList.size(); i++) {
            TableGroupingFieldQuery query = queryList.get(i);
            List<GroupingDataWrapper> groupingDataList = queryGroupingDataWrapper(context, query, parentQuery, i == lastIndex);
            lastGroupingData = TableGroupingDataHelper.mergeGroupingDataList(parentQuery, lastGroupingData, groupingDataList);
            parentQuery = query;
        }
        result.setGroups(TableGroupingDataHelper.collectionGroupingData(context.getModel(), groupingDataMap, queryList));
        TableGroupingHelper.computePaging(pagination, result);
    }

    private List<GroupingDataWrapper> queryGroupingDataWrapper(TableGroupingQueryContext<T> context, TableGroupingFieldQuery query, TableGroupingFieldQuery parentQuery, Boolean isLeaf) {
        List<T> list;
        if (query.isO2MField()) {
            list = queryGroupingDataByO2MField(context, query);
        } else if (query.isM2MField()) {
            list = queryGroupingDataByM2MField(context, query);
        } else {
            list = queryGroupingDataBySingleTableQuery(context, query);
        }
//        QueryWrapper<T> queryWrapper = context.generatorQueryWrapperWithGroupBy(query);
//        return queryPageAll(queryWrapper, (dataList) -> {
//            List<GroupingDataWrapper> sublist = new ArrayList<>();
//            for (T data : dataList) {
//                GroupingDataWrapper groupingDataWrapper = TableGroupingDataHelper.generatorGroupingDataWrapper(query, data, isLeaf);
//                TableGroupingFieldQuery parent = query.getParent();
//                if (parent != null) {
//                    groupingDataWrapper.setParentKey(TableGroupingDataHelper.getGroupKeyByData(parent, data));
//                    groupingDataWrapper.setParentField(parent.getField());
//                }
//                sublist.add(groupingDataWrapper);
//            }
//            return sublist;
//        });
        return new ArrayList<>();
    }

    private List<T> queryGroupingDataByO2MField(TableGroupingQueryContext<T> context, TableGroupingFieldQuery query) {
        List<Object> o2mDataList = TableGroupingHelper.queryO2MDataListByWrapper(query, null);
        List<String> referenceFields = query.getReferenceFields();
        List<List<Object>> inValues = new ArrayList<>(referenceFields.size());
        for (Object item : o2mDataList) {
            for (int i = 0; i < referenceFields.size(); i++) {
                if (inValues.size() < i + 1) {
                    inValues.add(new ArrayList<>());
                }
                String referenceField = referenceFields.get(i);
                List<Object> inValue = inValues.get(i);
                Object pkValue = FieldUtils.getFieldValue(item, referenceField);
                inValue.add(pkValue);
            }
        }
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapper();
        queryWrapper.in(query.getRelationColumns(), inValues.toArray(new List[0]));
        List<T> list = Models.origin().queryListByWrapper(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            list = Models.origin().listFieldQuery(list, query.getField());
        }
        return list;
    }

    private List<T> queryGroupingDataByM2MField(TableGroupingQueryContext<T> context, TableGroupingFieldQuery query) {
        List<Object> m2mDataList = TableGroupingHelper.queryM2MDataListByWrapper(query, null);
        List<String> throughRelationFields = query.getThroughRelationFields();
        List<List<Object>> inValues = new ArrayList<>(throughRelationFields.size());
        for (Object item : m2mDataList) {
            for (int i = 0; i < throughRelationFields.size(); i++) {
                if (inValues.size() < i + 1) {
                    inValues.add(new ArrayList<>());
                }
                String throughRelationField = throughRelationFields.get(i);
                List<Object> inValue = inValues.get(i);
                inValue.add(FieldUtils.getFieldValue(item, throughRelationField));
            }
        }
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapper();
        queryWrapper.in(query.getRelationColumns(), inValues.toArray(new List[0]));
        queryWrapper.setBatchSize(-1);
        List<T> list = Models.origin().queryListByWrapper(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            list = Models.origin().listFieldQuery(list, query.getField());
        }
        return list;
    }

    private List<T> queryGroupingDataBySingleTableQuery(TableGroupingQueryContext<T> context, TableGroupingFieldQuery query) {
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapper();
        return null;
    }
}
