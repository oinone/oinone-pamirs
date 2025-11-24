package pro.shushi.pamirs.grouping.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.runtime.executor.FieldPermissionExecutor;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.GroupingDataWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.entity.TableGroupingModel;
import pro.shushi.pamirs.grouping.model.GroupingData;
import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 表格分组帮助类
 *
 * @author Adamancy Zhang at 13:42 on 2025-11-17
 */
public class TableGroupingHelper {

    private TableGroupingHelper() {
        // reject create object
    }

    /**
     * 计算分页参数
     */
    public static <T> void computePaging(Pagination<T> pagination, TableGroupingResult result) {
        List<GroupingData> groups = result.getGroups();
        Long totalElements = pagination.getTotalElements();
        if (totalElements == null) {
            totalElements = Long.parseLong(String.valueOf(groups.size()));
            result.setTotalElements(totalElements);
        }
        int size = pagination.getSize().intValue();
        if (size < 0) {
            result.setTotalPages(1);
        } else {
            result.setTotalPages((int) (totalElements / size) + 1);
        }
    }

    /**
     * 内存分页
     */
    public static <T> void memoryPaging(Pagination<T> pagination, TableGroupingResult result) {
        List<GroupingData> groups = result.getGroups();
        int totalElements = groups.size();
        int size = pagination.getSize().intValue();
        if (totalElements == 0) {
            result.setTotalElements(0L);
            result.setTotalPages(0);
        } else if (size < 0) {
            result.setTotalElements((long) totalElements);
            result.setTotalPages(1);
        } else {
            result.setTotalElements((long) totalElements);
            result.setTotalPages((totalElements / size) + 1);
            int beginIndex = (pagination.getCurrentPage() - 1) * size;
            result.setGroups(new ArrayList<>(groups.subList(beginIndex, Math.min(beginIndex + size, groups.size()))));
        }
    }

    /**
     * 查询一级分组数据（不包含其他字段）
     * <p>
     * 使用前需要使用 isSingleTableQuery 方法判断是否可用
     */
    public static <T> List<T> queryFirstGroupingData(TableGroupingQueryContext<T> context, Pagination<T> pagination) {
        TableGroupingFieldQuery firstQuery = context.getQueryList().get(0);
        if (firstQuery.isO2MField()) {
            return queryGroupingDataByO2MField(context, firstQuery, pagination);
        } else if (firstQuery.isM2MField()) {
            return queryGroupingDataByM2MField(context, firstQuery, pagination);
        } else {
            return queryGroupingDataBySingleTableQuery(context, pagination, firstQuery);
        }
    }

    public static <T> List<T> queryGroupingDataBySingleTableQuery(TableGroupingQueryContext<T> context, Pagination<T> pagination, TableGroupingFieldQuery query) {
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapper();
        query.withGroupBy(queryWrapper);
        query.withOrderBy(queryWrapper);
        Long totalElements = Models.origin().count(queryWrapper);
        if (totalElements == null) {
            totalElements = 0L;
        }
        if (totalElements.compareTo(0L) <= 0) {
            pagination.setTotalElements(0L);
            return new ArrayList<>();
        }
        query.withSelect(queryWrapper);
        Pagination<T> page = new Pagination<>();
        pagination.to(page);
        page.setSortable(false);
        List<T> list = Models.origin().queryListByWrapper(page, queryWrapper);
        if (query.isSupportRelationQuery()) {
            List<T> relationDataList = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(relationDataList)) {
                Models.origin().listFieldQuery(relationDataList, query.getField());
            }
        }
        return list;
    }

    public static List<Object> queryO2MDataListByWrapper(TableGroupingFieldQuery query, Pagination<?> pagination) {
        List<String> referenceColumns = query.getReferenceColumns();
        QueryWrapper<Object> queryO2MWrapper = Pops.query();
        queryO2MWrapper.from(query.getReferences());
        queryO2MWrapper.select(WrapperHelper.getColumAsField(referenceColumns, query.getReferenceAsFields()));
        for (String referenceColumn : referenceColumns) {
            queryO2MWrapper.isNotNull(referenceColumn);
            queryO2MWrapper.groupBy(referenceColumn);
            query.withOrderBy(queryO2MWrapper, referenceColumn);
        }
        if (pagination == null) {
            return Models.origin().queryListByWrapper(queryO2MWrapper);
        } else {
            Pagination<Object> page = new Pagination<>();
            pagination.to(page);
            page.setSortable(false);
            return Models.origin().queryListByWrapper(page, queryO2MWrapper);
        }
    }

    public static <T> List<T> queryGroupingDataByO2MField(TableGroupingQueryContext<T> context, TableGroupingFieldQuery query, Pagination<T> pagination) {
        List<Object> o2mDataList = queryO2MDataListByWrapper(query, pagination);
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

    public static List<Object> queryM2MDataListByWrapper(TableGroupingFieldQuery query, Pagination<?> pagination) {
        List<String> throughRelationColumns = query.getThroughRelationColumns();
        QueryWrapper<Object> queryM2MWrapper = Pops.query();
        queryM2MWrapper.from(query.getThrough());
        queryM2MWrapper.select(WrapperHelper.getColumAsField(throughRelationColumns, query.getThroughRelationAsFields()));
        for (String throughRelationColumn : throughRelationColumns) {
            queryM2MWrapper.groupBy(throughRelationColumn);
            query.withOrderBy(queryM2MWrapper, throughRelationColumn);
        }
        if (pagination == null) {
            return Models.origin().queryListByWrapper(queryM2MWrapper);
        } else {
            Pagination<Object> page = new Pagination<>();
            pagination.to(page);
            page.setSortable(false);
            return Models.origin().queryListByWrapper(page, queryM2MWrapper);
        }
    }

    public static <T> List<T> queryGroupingDataByM2MField(TableGroupingQueryContext<T> context, TableGroupingFieldQuery query, Pagination<T> pagination) {
        List<Object> m2mDataList = queryM2MDataListByWrapper(query, pagination);
        List<String> throughRelationFields = query.getThroughRelationFields();
        List<List<Object>> relationInValues = new ArrayList<>(throughRelationFields.size());
        for (Object item : m2mDataList) {
            for (int i = 0; i < throughRelationFields.size(); i++) {
                if (relationInValues.size() < i + 1) {
                    relationInValues.add(new ArrayList<>());
                }
                String throughRelationField = throughRelationFields.get(i);
                List<Object> inValue = relationInValues.get(i);
                inValue.add(FieldUtils.getFieldValue(item, throughRelationField));
            }
        }
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapper();
        queryWrapper.in(query.getRelationColumns(), relationInValues.toArray(new List[0]));
        queryWrapper.setBatchSize(-1);
        List<T> list = Models.origin().queryListByWrapper(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            list = Models.origin().listFieldQuery(list, query.getField());
        }
        return list;
    }

    /**
     * 查询一级分组数据（不包含其他字段）
     * <p>
     * 使用前需要使用 isSingleTableQuery 方法判断是否可用
     */
    public static <T> Map<String, GroupingDataWrapper> queryFirstGroupingDataMap(TableGroupingQueryContext<T> context, Pagination<T> pagination, Boolean isLeaf) {
        TableGroupingFieldQuery firstQuery = context.getQueryList().get(0);
        List<T> list = queryFirstGroupingData(context, pagination);
        Map<String, GroupingDataWrapper> groupingDataMap = new LinkedHashMap<>();
        for (T data : list) {
            TableGroupingDataHelper.computeIfAbsent(groupingDataMap, firstQuery, data, isLeaf);
        }
        return groupingDataMap;
    }

    public static <T> List<GroupingData> fullDataConvertGroups(List<TableGroupingFieldQuery> queryList, String model, List<T> list) {
        return fullDataConvertGroups(queryList, model, list, false);
    }

    public static <T> List<GroupingData> fullDataConvertGroups(List<TableGroupingFieldQuery> queryList, String model, List<T> list, boolean addData) {
        Map<String, GroupingDataWrapper> groupingDataWrapperMap = new LinkedHashMap<>();
        TableGroupingDataHelper.generatorGroupingDataList(groupingDataWrapperMap, queryList, list, addData);
        FieldPermissionExecutor.filter(model, list);
        return TableGroupingDataHelper.collectionGroupingData(model, groupingDataWrapperMap, queryList);
    }

    public static <T> List<T> filter(List<T> list, List<TableGroupingFieldQuery> queryList) {
        for (TableGroupingFieldQuery query : queryList) {
            list = filter(list, query);
        }
        return list;
    }

    private static <T> List<T> filter(List<T> list, TableGroupingFieldQuery query) {
        String valueKey = query.getValueKey();
        List<T> results = new ArrayList<>();
        for (T data : list) {
            if (valueKey.equals(TableGroupingDataHelper.getGroupKeyByData(query, data))) {
                results.add(data);
            }
        }
        return results;
    }

    public static <T> List<GroupingData> fetchGroupingDataList(TableGroupingModel groupingModel, List<TableGroupingFieldQuery> queryList, QueryWrapper<T> queryWrapper) {
        Set<String> columns = new LinkedHashSet<>();
        List<String> needQueryRelationFields = new ArrayList<>();
        List<String> pkColumns = groupingModel.getPkColumns();
        if (CollectionUtils.isNotEmpty(pkColumns)) {
            columns.addAll(WrapperHelper.getColumAsFields(pkColumns, groupingModel.getPkAsFields()));
        }
        for (TableGroupingFieldQuery query : queryList) {
            String column = query.getColumn();
            if (StringUtils.isNotBlank(column)) {
                columns.add(WrapperHelper.getColumAsField(column, query.getAsField()));
            }
            List<String> relationColumns = query.getRelationColumns();
            if (CollectionUtils.isNotEmpty(relationColumns)) {
                columns.addAll(WrapperHelper.getColumAsFields(relationColumns, query.getRelationAsFields()));
            }
            if (query.isSupportRelationQuery()) {
                needQueryRelationFields.add(query.getField());
            }
        }
        queryWrapper.select(columns.toArray(new String[0]));
        String model = groupingModel.getModel();
        Map<String, GroupingDataWrapper> groupingDataMap = new LinkedHashMap<>();
        if (needQueryRelationFields.isEmpty()) {
            FetchUtil.fetchDataList(model, queryWrapper, (list) -> TableGroupingDataHelper.generatorGroupingDataList(groupingDataMap, queryList, list, false));
        } else {
            FetchUtil.fetchDataList(model, queryWrapper, (list) -> {
                // FIXME: zbh 20251121 可按需查询关联关系字段
                for (String field : needQueryRelationFields) {
                    list = Models.origin().listFieldQuery(list, field);
                }
                TableGroupingDataHelper.generatorGroupingDataList(groupingDataMap, queryList, list, false);
            });
        }
        return TableGroupingDataHelper.collectionGroupingData(model, groupingDataMap, queryList);
    }
}
