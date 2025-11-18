package pro.shushi.pamirs.grouping.query.grouping;

import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.GroupingDataWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.model.GroupingData;
import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.utils.TableGroupingDataHelper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 抽象表格分组查询
 *
 * @author Adamancy Zhang at 17:12 on 2025-11-14
 */
public abstract class AbstractTableGroupingQuery<T> implements TableGroupingQueryApi<T> {

    protected List<GroupingData> computeFirstGrouping(Pagination<T> pagination, List<GroupingData> groupingDataList) {
        int size = pagination.getSize().intValue();
        if (size < 0) {
            return groupingDataList;
        }
        int currentPage = pagination.getCurrentPage();
        if (currentPage == 1) {
            if (size >= groupingDataList.size()) {
                return groupingDataList;
            }
            return new ArrayList<>(groupingDataList.subList(0, Math.min(size + 1, groupingDataList.size() - 1)));
        } else {
            int beginIndex = (currentPage - 1) * size - 1;
            int endIndex = currentPage * size + 1;
            int maxIndex = groupingDataList.size() - 1;
            if (beginIndex <= maxIndex) {
                return groupingDataList.subList(beginIndex, Math.min(endIndex, maxIndex));
            }
        }
        return new ArrayList<>();
    }

    protected void computePaging(Pagination<T> pagination, TableGroupingResult result) {
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

    protected List<T> queryFirstGroupingData(TableGroupingQueryContext<T> context, Pagination<T> pagination) {
        TableGroupingFieldQuery firstQuery = context.getQueryList().get(0);
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapperWithGroupBy();
        queryWrapper.select(firstQuery.getColumnAsField());
        Pagination<T> page = new Pagination<>();
        pagination.to(page);
        page.setSize(page.getSize() + 2);
        page.setSortable(false);
        return Models.origin().queryListByWrapper(page, queryWrapper);
    }

    protected Map<String, GroupingDataWrapper> queryFirstGroupingDataMap(TableGroupingQueryContext<T> context, Pagination<T> pagination, Boolean isLeaf) {
        TableGroupingFieldQuery firstQuery = context.getQueryList().get(0);
        List<T> list = queryFirstGroupingData(context, pagination);
        Map<String, GroupingDataWrapper> groupingDataMap = new LinkedHashMap<>();
        for (T data : list) {
            TableGroupingDataHelper.computeIfAbsent(groupingDataMap, firstQuery, data, isLeaf);
        }
        return groupingDataMap;
    }

//    protected List<GroupingData> queryGroupingData(TableGroupingQueryContext<T> context, TableGroupingFieldQuery query, Boolean isLeaf) {
//        QueryWrapper<T> queryWrapper = context.generatorQueryWrapperWithGroupBy(query);
//        return queryPageAll(queryWrapper, (dataList) -> {
//            List<GroupingData> sublist = new ArrayList<>();
//            for (T data : dataList) {
//                sublist.add(TableGroupingDataHelper.generatorGroupingData(data, query, isLeaf));
//            }
//            return sublist;
//        });
//    }

    protected <R> List<R> queryPageAll(QueryWrapper<T> queryWrapper, Function<List<T>, List<R>> converter) {
        // FIXME: zbh 20251113 此处应该取模型批量配置
//        long batchSize = 500L;
        String model = queryWrapper.getModel();
//        Pagination<T> page = new Pagination<>();
//        page.setCurrentPage(1);
//        page.setSize(batchSize);
//        page.setModel(model);
//        Pagination<T> pagination = page.to(new Pagination<>());
//        Pagination<T> firstPage = Models.origin().queryPage(pagination, queryWrapper);
//        List<R> allList = converter.apply(firstPage.getContent());
//        if (batchSize < 0 || allList.size() < batchSize) {
//            return allList;
//        }
//        int totalPage = firstPage.getTotalPages();
//        for (int currentPage = 2; currentPage <= totalPage; currentPage++) {
//            pagination.setCurrentPage(currentPage);
//            Pagination<T> nextPage = Models.origin().queryPage(pagination, queryWrapper);
//            List<T> list = nextPage.getContent();
//            allList.addAll(converter.apply(list));
//            if (list.size() < batchSize) {
//                break;
//            }
//        }
//        return allList;
        return converter.apply(Models.origin().queryListByWrapper(queryWrapper));
    }
}
