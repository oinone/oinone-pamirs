package pro.shushi.pamirs.boot.web.service.impl;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.GroupOrderTypeEnum;
import pro.shushi.pamirs.boot.base.tmodel.*;
import pro.shushi.pamirs.boot.web.enmu.GroupingExpEnumerate;
import pro.shushi.pamirs.boot.web.service.GroupingService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.condition.Order;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Gesi at 17:10 on 2025/9/1
 */
@Service
public class GroupingServiceImpl implements GroupingService {

    public static final long QUERY_GROUP_ALL_DATA_LIMIT = 300;

    public static final String COUNT_FIELD_NAME = "COUNT";

    private static final TypeReference<Map<String, Object>> QUERY_DATA_TYPE_REF = new TypeReference<Map<String, Object>>() {
    };

    @Override
    public <T extends D> GroupResult<T> fetchGroupPage(Grouping<T> group, Pagination<T> page, IWrapper<T> wrapper, boolean isFetchData) {
        String model = group.getModel();
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (modelConfig == null) {
            throw PamirsException.construct(GroupingExpEnumerate.MODEL_NOT_FIND).errThrow();
        }
        group.setModelConfig(modelConfig);
        if (wrapper != null && !(wrapper instanceof QueryWrapper)) {
            throw PamirsException.construct(GroupingExpEnumerate.WRAPPER_CLASS_ERROR).errThrow();
        }
        QueryWrapper<T> queryWrapper = (QueryWrapper<T>) wrapper;
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper<>();
        }
        queryWrapper.from(model);
        String pageRsql = queryWrapper.getRsql();
        String pageQueryData = JsonUtils.toJSONString(queryWrapper.getQueryData() != null ? queryWrapper.getQueryData() : new HashMap<>());
        group.setPageRsql(pageRsql != null ? pageRsql : "");
        group.setPageQueryData(pageQueryData);

        // 数据量小于指定数量时直接返回全部
        enableFunctionCallSpi();
        Long count = Fun.run(model, FunctionConstants.countByWrapper, buildPageQueryWrapper(group));
        group.setTotalCount(count);
        if (count <= QUERY_GROUP_ALL_DATA_LIMIT) {
            return fetchAllData(group, page, wrapper);
        }

        if (!isFetchData) {
            return queryGroupInfo(group);
        } else {
            return queryGroupData(group, page);
        }
    }

    private <T extends D> GroupResult<T> fetchAllData(Grouping<T> group, Pagination<T> page, IWrapper<T> wrapper) {
        Sort sort = new Sort();
        List<Order> orderList = new ArrayList<>();

        sort.setOrders(orderList);
        page.setSort(sort);
        return null;
    }

    /**
     * 函数调用时走hook和扩展点等spi逻辑
     */
    private void enableFunctionCallSpi() {
        PamirsSession.directive().enableFromClient();
        PamirsSession.directive().enableHook();
        PamirsSession.directive().enableExtPoint();
    }

    private <T extends D> GroupResult<T> queryGroupInfo(final Grouping<T> group) {
        GroupResult<T> groupResult = new GroupResult<>();
        groupResult.setTotalElements(group.getTotalCount());

        consumeGroupSelectTree(group, null, (treePath) -> {
            QueryWrapper<?> queryWrapper = buildPageQueryWrapper(group);
            List<String> groupFields = new ArrayList<>(treePath.size() + 1);
            queryWrapper.and(andWrapper -> {
                for (Pair<ModelFieldConfig, GroupSelectField> treeNode : treePath) {
                    ModelFieldConfig modelFieldConfig = treeNode.getLeft();
                    GroupSelectField selectField = treeNode.getRight();
                    GroupOrderTypeEnum orderType = Optional.ofNullable(selectField.getGroupField().getOrderType()).orElse(GroupOrderTypeEnum.ASC);
                    groupFields.add(modelFieldConfig.getColumn());
                    queryWrapper.orderBy(true, GroupOrderTypeEnum.ASC.equals(orderType), modelFieldConfig.getColumn());
                    andWrapper.eq(modelFieldConfig.getColumn(), selectField.getGroupValue());
                }
            });
            queryWrapper.groupBy(groupFields.toArray(new String[0]));
            List<String> selectFields = new ArrayList<>(groupFields);
            selectFields.add("COUNT(*) " + COUNT_FIELD_NAME);
            queryWrapper.select(selectFields.toArray(new String[0]));

            enableFunctionCallSpi();
            List<T> dataList = Fun.run(group.getModel(), FunctionConstants.queryListByWrapper, queryWrapper);
            fullGroupInfo(group, groupResult, dataList, true);
        });

        return groupResult;
    }

    private <T extends D> GroupResult<T> queryGroupData(final Grouping<T> group, Pagination<?> page) {
        GroupResult<T> groupResult = new GroupResult<>();
        groupResult.setTotalElements(group.getTotalCount());
        return groupResult;
    }

    /**
     * 构建所有已选查询分组的查询条件
     */
    private <T extends D> void appendGroupPageWhereCondition(final Grouping<T> group, QueryWrapper<?> queryWrapper) {
        List<GroupSelectField> selectGroupFields = group.getSelectGroupFields();
        if (CollectionUtils.isEmpty(selectGroupFields)) {
            return;
        }
        queryWrapper.and(andWrapper -> {
            consumeGroupSelectTree(group, null, (treePath) -> {
                andWrapper.or(orWrapper -> {
                    for (Pair<ModelFieldConfig, GroupSelectField> groupColumnValue : treePath) {
                        orWrapper.eq(groupColumnValue.getLeft().getColumn(), groupColumnValue.getRight().getGroupValue());
                    }
                });
            });
        });
    }

    private <T extends D> QueryWrapper<T> buildPageQueryWrapper(Grouping<T> group) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.from(group.getModel())
                .setRsql(group.getPageRsql())
                .setQueryData(JsonUtils.parseObject(group.getPageQueryData(), QUERY_DATA_TYPE_REF));
        return queryWrapper;
    }

    private <T extends D> void consumeGroupSelectTree(
            Grouping<T> group,
            Consumer<Pair<ModelFieldConfig, GroupSelectField>> treeNodeConsumer,
            Consumer<List<Pair<ModelFieldConfig, GroupSelectField>>> treePathConsumer
    ) {
        if (CollectionUtils.isEmpty(group.getSelectGroupFields())) {
            return;
        }
        for (GroupSelectField selectGroupField : group.getSelectGroupFields()) {
            consumeGroupSelectTree0(group, treeNodeConsumer, treePathConsumer, selectGroupField, new ArrayList<>());
        }
    }

    private <T extends D> void consumeGroupSelectTree0(
            Grouping<T> group,
            Consumer<Pair<ModelFieldConfig, GroupSelectField>> treeNodeConsumer,
            Consumer<List<Pair<ModelFieldConfig, GroupSelectField>>> treePathConsumer,
            GroupSelectField currentField, List<Pair<ModelFieldConfig, GroupSelectField>> groupColumnValues
    ) {
        GroupField groupField = currentField.getGroupField();
        String field = groupField.getField();
        ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(field);
        if (modelFieldConfig == null) {
            throw PamirsException.construct(GroupingExpEnumerate.FIELD_NOT_FIND).appendMsg("模型" + group.getModel() + "字段" + field + "找不到").errThrow();
        }
        Pair<ModelFieldConfig, GroupSelectField> treeNode = Pair.of(modelFieldConfig, currentField);

        if (treeNodeConsumer != null) {
            treeNodeConsumer.accept(treeNode);
        }

        if (CollectionUtils.isEmpty(currentField.getChildGroupSelectFields())) {
            groupColumnValues.add(treeNode);
            if (treePathConsumer != null) {
                treePathConsumer.accept(groupColumnValues);
            }
            groupColumnValues.remove(groupColumnValues.size() - 1);
            return;
        }

        groupColumnValues.add(treeNode);
        for (GroupSelectField childGroupSelectField : currentField.getChildGroupSelectFields()) {
            consumeGroupSelectTree0(group, treeNodeConsumer, treePathConsumer, childGroupSelectField, groupColumnValues);
        }
        groupColumnValues.remove(groupColumnValues.size() - 1);
    }

    private <T extends D> void fullGroupInfo(Grouping<T> group, GroupResult<T> groupResult, List<T> dataList, boolean isFromGroupCount) {
        List<GroupField> groupFields = group.getGroupFields();
        List<GroupInfo<T>> level1Groups = groupResult.getGroups();
        groupResult.setGroups(level1Groups);
        for (T data : dataList) {
            GroupInfo<T> groupInfo = null;
            List<GroupInfo<T>> beforeLevelGroups = level1Groups;
            for (int i = 0; i < groupFields.size(); i++) {
                GroupField groupField = groupFields.get(i);
                if (data.get_d().containsKey(groupField.getField())) {
                    Object value = data.get_d().get(groupField.getField());
                    for (GroupInfo<T> beforeLevelGroup : beforeLevelGroups) {
                        if (Objects.equals(value, beforeLevelGroup.getValue())) {
                            groupInfo = beforeLevelGroup;
                            break;
                        }
                    }
                    if (groupInfo == null) {
                        groupInfo = new GroupInfo<>();
                        groupInfo.setField(groupField.getField());
                        groupInfo.setValue(value);
                        groupInfo.setGroups(new ArrayList<>());
                        beforeLevelGroups.add(groupInfo);
                    }

                    beforeLevelGroups = groupInfo.getGroups();
                } else {
                    break;
                }
            }

            if (groupInfo != null) {
                if (groupInfo.getDataList() == null) {
                    groupInfo.setDataList(new ArrayList<>());
                }
                groupInfo.getDataList().add(data);
            }
        }
    }

}
