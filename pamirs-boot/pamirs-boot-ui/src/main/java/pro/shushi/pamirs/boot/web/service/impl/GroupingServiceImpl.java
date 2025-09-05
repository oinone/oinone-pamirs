package pro.shushi.pamirs.boot.web.service.impl;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.tmodel.*;
import pro.shushi.pamirs.boot.web.enmu.GroupingExpEnumerate;
import pro.shushi.pamirs.boot.web.service.GroupingService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.gateways.hook.RsqlParseHook;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Order;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Gesi at 17:10 on 2025/9/1
 */
@Service
public class GroupingServiceImpl implements GroupingService {

    private static final TypeReference<Map<String, Object>> QUERY_DATA_TYPE_REF = new TypeReference<Map<String, Object>>() {
    };

    @Autowired
    private RsqlParseHook rsqlParseHook;

    @Override
    public <T> GroupResult<T> fetchGroupPage(Grouping<T> group, Pagination<T> page) {
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

        Long count = Models.origin().count(parseQueryWrapper(buildPageQueryWrapper(group)));
        group.setTotalDataCount(count);
        return queryGroupInfo(group, page);
    }

    private <T> GroupResult<T> queryGroupInfo(final Grouping<T> group, Pagination<?> page) {
        GroupResult<T> groupResult = new GroupResult<>();
        groupResult.setTotalDataCount(group.getTotalDataCount());

        QueryWrapper<T> queryWrapper = buildPageQueryWrapper(group);
        if (Boolean.TRUE.equals(group.getNeedPagination())) {
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
        fullGroupInfo(group, groupResult, paginationResult.getContent(), (groupInfo) -> {
            // todo 加统计函数实现
        });
        if (!Boolean.TRUE.equals(group.getNeedPagination())) {
            groupResult.setTotalElements(groupResult.getGroups() != null ? groupResult.getGroups().size() : 0L);
        }
        return groupResult;
    }

    private <T> Pagination<T> addGroupPaginationCondition(Grouping<T> group, QueryWrapper<T> queryWrapper, int pageNo, long pageSize) {
        GroupField firstGroupField = group.getGroupFields().get(0);
        ModelFieldConfig firstModelFieldConfig = group.getModelFieldConfig(firstGroupField.getField());

        Pagination<T> pagination = new Pagination<>(pageNo, pageSize);
        SortDirectionEnum orderType = Optional.ofNullable(firstGroupField.getOrderType()).orElse(SortDirectionEnum.ASC);
        QueryWrapper<T> groupQueryWrapper = buildPageQueryWrapper(group);
        groupQueryWrapper.select(firstModelFieldConfig.getColumn());
        groupQueryWrapper.orderBy(true, SortDirectionEnum.ASC.equals(orderType), firstModelFieldConfig.getColumn());
        pagination = Models.origin().queryPage(pagination, parseQueryWrapper(groupQueryWrapper));

        if (CollectionUtils.isNotEmpty(pagination.getContent())) {
            Pagination<T> finalPagination = pagination;
            queryWrapper.and(andWrapper -> {
                for (T data : finalPagination.getContent()) {
                    Object groupValue = FieldUtils.getFieldValue(data, firstGroupField.getField());
                    if (groupValue == null) {
                        andWrapper.or().isNull(firstModelFieldConfig.getColumn());
                    } else {
                        andWrapper.or().eq(firstModelFieldConfig.getColumn(), groupValue);
                    }
                }
            });
        }

        return pagination;
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

    private <T> void fullGroupInfo(Grouping<T> group, GroupResult<T> groupResult, List<T> dataList, Consumer<GroupInfo<T>> statisticConsumer) {
        List<GroupField> groupFields = group.getGroupFields();

        Map<List<GroupInfo.GroupPathNode>, GroupInfo<T>> groupPathMap = new LinkedHashMap<>();
        Set<List<GroupInfo.GroupPathNode>> firstGroupPathList = new LinkedHashSet<>();
        Set<List<GroupInfo.GroupPathNode>> lastGroupPathList = new HashSet<>();

        // 加载之前已加载过的分组信息
        List<GroupInfo<T>> beforeGroupFields = groupResult.getGroups();
        LinkedList<GroupInfo<T>> beforeGroupFieldStack = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(beforeGroupFields)) {
            beforeGroupFieldStack.addAll(beforeGroupFields);
        }
        while (!beforeGroupFieldStack.isEmpty()) {
            GroupInfo<T> groupInfo = beforeGroupFieldStack.remove(0);
            groupPathMap.put(groupInfo.getGroupPath(), groupInfo);
            if (CollectionUtils.isNotEmpty(groupInfo.getGroups())) {
                beforeGroupFieldStack.addAll(groupInfo.getGroups());
            }
        }

        for (T data : dataList) {
            List<GroupInfo.GroupPathNode> groupPath = null;
            for (int i = 0; i < groupFields.size(); i++) {
                GroupField groupField = groupFields.get(i);
                GroupInfo<T> parentGroupInfo = groupPathMap.get(groupPath);

                Object value = FieldUtils.getFieldValue(data, groupField.getField());
                if (groupPath == null) {
                    groupPath = new ArrayList<>();
                } else {
                    groupPath = new ArrayList<>(groupPath);
                }
                groupPath.add(new GroupInfo.GroupPathNode(groupField, value));

                // 判断当前分组是否已存在
                GroupInfo<T> groupInfo = groupPathMap.get(groupPath);
                boolean isCreateGroup = false;
                if (groupInfo == null) {
                    groupInfo = new GroupInfo<>();
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
        Map<Integer, List<List<GroupInfo.GroupPathNode>>> groupPathMapByNodeNum = new HashMap<>();
        for (List<GroupInfo.GroupPathNode> groupPath : groupPathMap.keySet()) {
            groupPathMapByNodeNum.putIfAbsent(groupPath.size(), new ArrayList<>());
            groupPathMapByNodeNum.get(groupPath.size()).add(groupPath);
        }

        List<Integer> groupPathNodeNumList = new ArrayList<>(groupPathMapByNodeNum.keySet());
        groupPathNodeNumList.sort((n1, n2) -> Integer.compare(n2, n1));

        for (Integer nodeNum : groupPathNodeNumList) {
            List<List<GroupInfo.GroupPathNode>> groupPathList = groupPathMapByNodeNum.get(nodeNum);
            for (List<GroupInfo.GroupPathNode> groupPath : groupPathList) {
                String path = groupPath.stream().map(node -> node.field.getField() + "-" + node.value).collect(Collectors.joining(","));
                // 这里的子级groupInfo一定是都填充完成的
                GroupInfo<T> groupInfo = groupPathMap.get(groupPath);
                groupInfo.setValueStr(GroupInfo.stringifyValue(groupInfo, groupInfo.getValue()));
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
                    // 计算统计函数
                    if (statisticConsumer != null) {
                        statisticConsumer.accept(groupInfo);
                    }
                    if (statisticConsumer != null) {
                        statisticConsumer.accept(groupInfo);
                    }

                    // 序列化统计结果
                    groupInfo.setDataStatisticStr(GroupInfo.stringifyStatisticResult(group, groupInfo, groupInfo.getDataStatistic()));
                }
            }
        }

        // 序列化叶子节点数据List
        for (List<GroupInfo.GroupPathNode> lastGroupPath : lastGroupPathList) {
            GroupInfo<T> lastGroupInfo = groupPathMap.get(lastGroupPath);
            if (lastGroupInfo.getDataList() != null) {
                lastGroupInfo.setDataListStr(JsonUtils.toJSONString(lastGroupInfo.getDataList()));
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
