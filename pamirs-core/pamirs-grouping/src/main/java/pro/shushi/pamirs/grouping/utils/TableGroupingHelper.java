package pro.shushi.pamirs.grouping.utils;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.auth.api.runtime.executor.FieldPermissionExecutor;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.GroupingDataWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.model.GroupingData;
import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

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

    public static <T> void computePaging(Pagination<T> pagination, TableGroupingResult result) {
        List<GroupingData> groups = result.getGroups();
        long totalElements = Long.parseLong(String.valueOf(groups.size()));
        result.setTotalElements(totalElements);
        Long size = pagination.getSize();
        if (size < 0) {
            result.setTotalPages(1);
        } else {
            result.setTotalPages((int) (totalElements / size) + 1);
        }
    }

    /**
     * 查询一级分组数据（不包含其他字段）
     * <p>
     * 使用前需要使用 isSingleTableQuery 方法判断是否可用
     */
    public static <T> List<T> queryFirstGroupingData(TableGroupingQueryContext<T> context, Pagination<T> pagination) {
        TableGroupingFieldQuery firstQuery = context.getQueryList().get(0);
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapper();
        firstQuery.withGroupBy(queryWrapper);
        Pagination<T> page = new Pagination<>();
        pagination.to(page);
        page.setSortable(false);
        // FIXME: zbh 20251117 此处需使用 queryPage 查询数据
        List<T> list = Models.origin().queryListByWrapper(page, queryWrapper);
        if (firstQuery.isRelationOneField()) {
            Models.origin().listFieldQuery(list.stream().filter(Objects::nonNull).collect(Collectors.toList()), firstQuery.getField());
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

    public static <T> List<T> fetchGroupingDataList(List<TableGroupingFieldQuery> queryList, QueryWrapper<T> queryWrapper) {
        return fetchGroupingDataList(queryList, queryWrapper, true);
    }

    public static <T> List<T> fetchGroupingDataList(List<TableGroupingFieldQuery> queryList, QueryWrapper<T> queryWrapper, boolean filter) {
        List<TableGroupingFieldQuery> memoryQueryList = new ArrayList<>();
        for (TableGroupingFieldQuery query : queryList) {
            if (query.isSingleTableQuery()) {
                query.withWhere(queryWrapper);
            } else {
                memoryQueryList.add(query);
            }
        }
        List<T> list = Models.origin().queryListByWrapper(queryWrapper);
        if (CollectionUtils.isEmpty(list) || memoryQueryList.isEmpty()) {
            return list;
        }
        for (TableGroupingFieldQuery memoryQuery : memoryQueryList) {
            list = Models.origin().listFieldQuery(list, memoryQuery.getField());
        }
        if (filter) {
            list = filter(list, memoryQueryList);
        }
        return list;
    }
}
