package pro.shushi.pamirs.boot.web.service.impl;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.GroupStatisticTypeEnum;
import pro.shushi.pamirs.boot.base.tmodel.*;
import pro.shushi.pamirs.boot.web.enmu.GroupingExpEnumerate;
import pro.shushi.pamirs.boot.web.service.GroupingService;
import pro.shushi.pamirs.boot.web.spi.api.GroupStatisticApi;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.gateways.hook.RsqlParseHook;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Order;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author Gesi at 17:10 on 2025/9/1
 */
@Service
public class GroupingServiceImpl implements GroupingService {

    private static final long GROUP_LAZY_LOAD_DATA_LIMIT = 300;

    private static final TypeReference<Map<String, Object>> QUERY_DATA_TYPE_REF = new TypeReference<Map<String, Object>>() {
    };

    @Autowired
    private RsqlParseHook rsqlParseHook;

    @Override
    public <T> GroupResult<T> fetchGroupPage(Grouping<T> group, Pagination<T> page) {
        loadGroupBaseInfo(group);
        Long count = Models.origin().count(parseQueryWrapper(buildPageQueryWrapper(group)));
        group.setTotalDataCount(count);
        return queryGroupInfo(group, page);
    }

    @Override
    public <T> GroupResult<T> fetchStatistics(Grouping<T> group) {
        List<GroupPath<T>> expandGroupPaths = group.getExpandGroupPaths();
        if (CollectionUtils.isEmpty(expandGroupPaths)) {
            throw PamirsException.construct(GroupingExpEnumerate.STATISTICS_PATHS_IS_NULL).errThrow();
        }
        loadGroupBaseInfo(group);
        QueryWrapper<T> queryWrapper = buildPageQueryWrapper(group);
        addGroupExpandCondition(group, queryWrapper, expandGroupPaths);
        Pagination<T> paginationResult = Models.origin().queryPage(new Pagination<>(1, -1), parseQueryWrapper(queryWrapper));

        GroupResult<T> groupResult = new GroupResult<>();
        groupResult.setExpandGroupStatistic(new HashMap<>());
        group.unsetExpandGroupPaths();
        group.setTotalDataCount(Long.MAX_VALUE);
        fullGroupInfo(group, groupResult, paginationResult.getContent(), statisticFunction());
        groupResult.setExpandGroupStatisticStr(new ArrayList<>(expandGroupPaths.size()));
        for (GroupPath<T> expandGroupPath : expandGroupPaths) {
            groupResult.getExpandGroupStatisticStr().add(groupResult.getExpandGroupStatistic().get(expandGroupPath));
        }
        return groupResult;
    }

    @Override
    public <T> GroupResult<T> fetchGroupData(Grouping<T> group) {
        List<GroupPath<T>> expandGroupPaths = group.getExpandGroupPaths();
        if (CollectionUtils.isEmpty(expandGroupPaths)) {
            throw PamirsException.construct(GroupingExpEnumerate.LAZY_LOAD_PATHS_IS_NULL).errThrow();
        }
        loadGroupBaseInfo(group);

        QueryWrapper<T> queryWrapper = buildPageQueryWrapper(group);
        addGroupExpandCondition(group, queryWrapper, expandGroupPaths);
        Pagination<T> paginationResult = Models.origin().queryPage(new Pagination<>(1, -1), parseQueryWrapper(queryWrapper));

        GroupResult<T> groupResult = new GroupResult<>();
        groupResult.setExpandGroupData(new HashMap<>());
        group.setTotalDataCount(0L);
        fullGroupInfo(group, groupResult, paginationResult.getContent(), null);
        groupResult.setExpandGroupDataStr(new ArrayList<>(expandGroupPaths.size()));
        for (GroupPath<T> expandGroupPath : expandGroupPaths) {
            groupResult.getExpandGroupDataStr().add(groupResult.getExpandGroupData().get(expandGroupPath));
        }
        groupResult.unsetGroups();
        return groupResult;
    }

    private <T> void loadGroupBaseInfo(Grouping<T> group) {
        String model = group.getModel();
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (modelConfig == null) {
            throw PamirsException.construct(GroupingExpEnumerate.MODEL_NOT_FIND).errThrow();
        }
        group.setModelConfig(modelConfig);
        ConditionQueryWrapper queryWrapper = group.getQueryWrapper();
        if (queryWrapper == null) {
            queryWrapper = new ConditionQueryWrapper();
        }
        queryWrapper.setModel(model);

        if (CollectionUtils.isNotEmpty(group.getExpandGroupPaths())) {
            for (GroupPath<T> expandGroupPath : group.getExpandGroupPaths()) {
                for (GroupPathNode<T> groupPathNode : expandGroupPath.getNodeList()) {
                    groupPathNode.setGroup(group);
                }
            }
        }
    }

    private <T> GroupResult<T> queryGroupInfo(final Grouping<T> group, Pagination<?> page) {
        GroupResult<T> groupResult = new GroupResult<>();
        groupResult.setTotalDataCount(group.getTotalDataCount());

        QueryWrapper<T> queryWrapper = buildPageQueryWrapper(group);
        boolean needPagination = page.getSize() != null && page.getSize() >= 0;
        if (needPagination) {
            Pagination<T> pagination = addGroupPaginationCondition(group, queryWrapper, page.getCurrentPage(), page.getSize());
            groupResult.setTotalPages(pagination.getTotalPages());
            groupResult.setTotalElements(pagination.getTotalElements());
        } else {
            groupResult.setTotalPages(1);
        }

        for (GroupField groupField : group.getGroupFields()) {
            ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(groupField.getField());
            SortDirectionEnum orderType = Optional.ofNullable(groupField.getOrderType()).orElse(SortDirectionEnum.ASC);
            queryWrapper.orderBy(true, SortDirectionEnum.ASC.equals(orderType), modelFieldConfig.getColumn());
        }
        Sort sort = group.getQueryWrapper().getSort();
        if (Boolean.TRUE.equals(page.getSortable()) && sort != null && sort.getOrders() != null) {
            for (Order order : sort.getOrders()) {
                ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(order.getField());
                SortDirectionEnum orderType = Optional.ofNullable(order.getDirection()).orElse(SortDirectionEnum.ASC);
                queryWrapper.orderBy(true, SortDirectionEnum.ASC.equals(orderType), modelFieldConfig.getColumn());
            }
        }
        Pagination<T> paginationResult = Models.origin().queryPage(new Pagination<>(1, group.getTotalDataCount()), parseQueryWrapper(queryWrapper));
        fullGroupInfo(group, groupResult, paginationResult.getContent(), statisticFunction());
        if (!needPagination) {
            groupResult.setTotalElements(groupResult.getGroups() != null ? groupResult.getGroups().size() : 0L);
        }
        return groupResult;
    }

    private <T> BiConsumer<Grouping<T>, GroupInfo<T>> statisticFunction() {
        return (group, groupInfo) -> {
            List<GroupField> statisticFields = group.getStatisticFields();
            if (statisticFields != null) {
                Map<String, Object> statisticValues = new HashMap<>(statisticFields.size());
                for (GroupField statisticField : statisticFields) {
                    GroupStatisticTypeEnum statisticType =
                            Optional.ofNullable(statisticField.getStatisticType())
                                    .orElse(GroupStatisticTypeEnum.NONE);
                    List<T> dataList = groupInfo.getDataList();
                    List<?> fieldDataList;
                    if (dataList != null) {
                        fieldDataList = dataList.stream().map(data -> {
                            if (data == null) {
                                return null;
                            }
                            return FieldUtils.getFieldValue(data, statisticField.getField());
                        }).collect(Collectors.toList());
                    } else {
                        fieldDataList = null;
                    }
                    Object statisticValue = Spider
                            .getExtension(GroupStatisticApi.class, statisticType.getValue())
                            .statistic(group, groupInfo, statisticField, fieldDataList);
                    statisticValues.put(statisticField.getField(), statisticValue);
                }
                groupInfo.setDataStatistic(statisticValues);
            }
        };
    }

    private <T> Pagination<T> addGroupPaginationCondition(Grouping<T> group, QueryWrapper<T> queryWrapper, int pageNo, long pageSize) {
        GroupField firstGroupField = group.getGroupFields().get(0);
        ModelFieldConfig firstModelFieldConfig = group.getModelFieldConfig(firstGroupField.getField());

        Pagination<T> pagination = new Pagination<>(pageNo, pageSize);
        pagination.setSortable(false);
        SortDirectionEnum orderType = Optional.ofNullable(firstGroupField.getOrderType()).orElse(SortDirectionEnum.ASC);
        QueryWrapper<T> groupQueryWrapper = buildPageQueryWrapper(group);
        groupQueryWrapper.isNotNull(firstModelFieldConfig.getColumn());
        groupQueryWrapper.select(firstModelFieldConfig.getColumn());
        groupQueryWrapper.groupBy(firstModelFieldConfig.getColumn());
        groupQueryWrapper.orderBy(true, SortDirectionEnum.ASC.equals(orderType), firstModelFieldConfig.getColumn());
        pagination = Models.origin().queryPage(pagination, parseQueryWrapper(groupQueryWrapper));
        boolean needGroupNullValue;
        if (pagination.getContent() == null) {
            pagination.setContent(new ArrayList<>());
        }

        QueryWrapper<T> groupNullQueryWrapper = buildPageQueryWrapper(group);
        Pagination<T> nullPagination = new Pagination<>(1, 1);
        nullPagination.setSortable(false);
        groupNullQueryWrapper.isNull(firstModelFieldConfig.getColumn());
        groupNullQueryWrapper.select(firstModelFieldConfig.getColumn());
        nullPagination = Models.origin().queryPage(nullPagination, parseQueryWrapper(groupNullQueryWrapper));
        if (nullPagination.getTotalElements() > 0) {
            pagination.setTotalElements(pagination.getTotalElements() + 1);
        }

        needGroupNullValue = pagination.getContent().size() < pageSize && CollectionUtils.isNotEmpty(nullPagination.getContent());
        if (pageNo <= pagination.getTotalPages()) {
            Pagination<T> finalPagination = pagination;
            queryWrapper.and(andWrapper -> {
                List<Object> groupValueList =
                        finalPagination.getContent().stream().map(data -> FieldUtils.getFieldValue(data, firstGroupField.getField())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(groupValueList)) {
                    andWrapper.in(firstModelFieldConfig.getColumn(), groupValueList);
                }
                if (needGroupNullValue) {
                    andWrapper.or().isNull(firstModelFieldConfig.getColumn());
                }
            });
        } else {
            queryWrapper.eq("1", "0");
        }

        return pagination;
    }

    private <T> void addGroupExpandCondition(Grouping<T> group, QueryWrapper<T> queryWrapper, List<GroupPath<T>> expandGroupPaths) {
        queryWrapper.and(andWrapper -> {
            for (GroupPath<T> expandGroupPath : expandGroupPaths) {
                andWrapper.or().and(pathAndWrapper -> {
                    for (GroupPathNode<T> pathNode : expandGroupPath.getNodeList()) {
                        String column = group.getModelFieldConfig(pathNode.getField()).getColumn();
                        Object value = pathNode.getValue();
                        if (value != null) {
                            pathAndWrapper.eq(column, value);
                        } else {
                            pathAndWrapper.isNull(column);
                        }
                    }
                });
            }
        });
    }

    private <T> QueryWrapper<T> buildPageQueryWrapper(Grouping<T> group) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.from(group.getModel())
                .setRsql(group.getQueryWrapper().getRsql())
                .setQueryData(JsonUtils.parseObject(JsonUtils.toJSONString(group.getQueryWrapper().getQueryData()), QUERY_DATA_TYPE_REF));
        return queryWrapper;
    }

    private <T> QueryWrapper<T> parseQueryWrapper(final QueryWrapper<T> queryWrapper) {
        rsqlParseHook.parse(queryWrapper, queryWrapper.getModel());
        return queryWrapper;
    }

    private <T> void fullGroupInfo(Grouping<T> group, GroupResult<T> groupResult, List<T> dataList, BiConsumer<Grouping<T>, GroupInfo<T>> statisticConsumer) {
        List<GroupField> groupFields = group.getGroupFields();

        Map<GroupPath<T>, GroupInfo<T>> groupPathMap = new LinkedHashMap<>();
        Set<GroupPath<T>> firstGroupPathList = new LinkedHashSet<>();
        Set<GroupPath<T>> lastGroupPathList = new HashSet<>();

        // 加载之前已加载过的分组信息
        List<GroupInfo<T>> beforeGroupFields = groupResult.getGroups();
        LinkedList<GroupInfo<T>> beforeGroupFieldStack = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(beforeGroupFields)) {
            beforeGroupFieldStack.addAll(beforeGroupFields);
        }
        while (!beforeGroupFieldStack.isEmpty()) {
            GroupInfo<T> groupInfo = beforeGroupFieldStack.remove(0);
            groupPathMap.put(new GroupPath<>(groupInfo.getGroupPath()), groupInfo);
            if (CollectionUtils.isNotEmpty(groupInfo.getGroups())) {
                beforeGroupFieldStack.addAll(groupInfo.getGroups());
            }
        }

        for (T data : dataList) {
            GroupPath<T> groupPath = null;
            for (int i = 0; i < groupFields.size(); i++) {
                GroupField groupField = groupFields.get(i);
                GroupInfo<T> parentGroupInfo = groupPathMap.get(groupPath);

                Object value = FieldUtils.getFieldValue(data, groupField.getField());
                if (groupPath == null) {
                    groupPath = new GroupPath<>();
                } else {
                    groupPath = new GroupPath<>(groupPath);
                }
                groupPath.addNode(new GroupPathNode<>(group, groupField.getField(), value));

                // 判断当前分组是否已存在
                GroupInfo<T> groupInfo = groupPathMap.get(groupPath);
                boolean isCreateGroup = false;
                if (groupInfo == null) {
                    groupInfo = new GroupInfo<>();
                    groupInfo.setIsLeaf(false);
                    groupInfo.setGroupPath(groupPath);
                    groupInfo.setField(groupField.getField());
                    groupInfo.setValue(value);
                    groupInfo.setGroupField(groupField);
                    groupPathMap.put(groupPath, groupInfo);
                    isCreateGroup = true;
                }

                // 将当前分组信息放到父级分组里
                if (parentGroupInfo != null && isCreateGroup) {
                    if (parentGroupInfo.getGroups() == null) {
                        parentGroupInfo.setGroups(new ArrayList<>());
                    }
                    parentGroupInfo.getGroups().add(groupInfo);
                }
                if (i == 0) {
                    firstGroupPathList.add(groupPath);
                }
            }

            // 最终获取到的当前数据所属最后一级分组信息
            GroupInfo<T> lastGroupInfo = groupPathMap.get(groupPath);
            if (lastGroupInfo.getDataList() == null) {
                lastGroupInfo.setDataList(new ArrayList<>());
            }
            lastGroupInfo.getDataList().add(data);
            lastGroupPathList.add(groupPath);
        }

        // 填充分组信息
        // 分组路径长先处理，确保处理父分组时其下面的子分组一定处理过
        Map<Integer, List<GroupPath<T>>> groupPathMapByNodeNum = new HashMap<>();
        for (GroupPath<T> groupPath : groupPathMap.keySet()) {
            groupPathMapByNodeNum.putIfAbsent(groupPath.size(), new ArrayList<>());
            groupPathMapByNodeNum.get(groupPath.size()).add(groupPath);
        }

        List<Integer> groupPathNodeNumList = new ArrayList<>(groupPathMapByNodeNum.keySet());
        groupPathNodeNumList.sort((n1, n2) -> Integer.compare(n2, n1));

        for (Integer nodeNum : groupPathNodeNumList) {
            List<GroupPath<T>> groupPathList = groupPathMapByNodeNum.get(nodeNum);
            for (GroupPath<T> groupPath : groupPathList) {
                // 这里的子级groupInfo一定是都填充完成的
                GroupInfo<T> groupInfo = groupPathMap.get(groupPath);
                groupInfo.setValueStr(GroupInfo.stringifyValue(group.getModelFieldConfig(groupInfo.getField()), groupInfo.getValue()));
                List<GroupInfo<T>> childGroups = groupInfo.getGroups();
                moveGroupNullValueToLast(childGroups);
                if (CollectionUtils.isNotEmpty(childGroups)) {
                    List<T> groupDataList = new ArrayList<>();
                    for (GroupInfo<T> childGroup : childGroups) {
                        if (CollectionUtils.isNotEmpty(childGroup.getDataList())) {
                            groupDataList.addAll(childGroup.getDataList());
                        }
                    }
                    groupInfo.setDataList(groupDataList);
                }

                // 计算统计函数
                if (statisticConsumer != null) {
                    statisticConsumer.accept(group, groupInfo);
                }
                // 序列化统计结果
                groupInfo.setDataStatisticStr(groupInfo.getDataStatistic() != null ? JsonUtils.toJSONString(groupInfo.getDataStatistic()) : null);
                if (groupResult.getExpandGroupStatistic() != null) {
                    groupResult.getExpandGroupStatistic().put(groupPath, groupInfo.getDataStatisticStr());
                }
            }
        }

        // 序列化叶子节点数据List
        for (GroupPath<T> lastGroupPath : lastGroupPathList) {
            GroupInfo<T> lastGroupInfo = groupPathMap.get(lastGroupPath);
            lastGroupInfo.setIsLeaf(true);
            if (lastGroupInfo.getDataList() != null && group.getTotalDataCount() != null && (group.getTotalDataCount() <= GROUP_LAZY_LOAD_DATA_LIMIT || group.containsExpandPath(lastGroupPath))) {
                lastGroupInfo.setDataListStr(lastGroupInfo.getDataList() != null ? PamirsDataUtils.toJSONString(group.getModel(), lastGroupInfo.getDataList()) : null);
                if (groupResult.getExpandGroupData() != null) {
                    groupResult.getExpandGroupData().put(lastGroupPath, lastGroupInfo.getDataListStr());
                }
            }
        }

        groupResult.setGroups(firstGroupPathList.stream().map(groupPathMap::get).collect(Collectors.toList()));
        moveGroupNullValueToLast(groupResult.getGroups());
    }

    private <T> void moveGroupNullValueToLast(List<GroupInfo<T>> groups) {
        if (CollectionUtils.isNotEmpty(groups) && groups.get(0).getValue() == null) {
            groups.add(groups.remove(0));
        }
    }

}
