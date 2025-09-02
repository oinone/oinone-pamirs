package pro.shushi.pamirs.boot.web.service.impl;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.tmodel.GroupField;
import pro.shushi.pamirs.boot.base.tmodel.GroupResult;
import pro.shushi.pamirs.boot.base.tmodel.GroupSelectField;
import pro.shushi.pamirs.boot.base.tmodel.Grouping;
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
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public <T> GroupResult fetchGroupPage(Grouping group, Pagination<T> page, IWrapper<T> wrapper, boolean isFetchData) {
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

        List<GroupSelectField> selectGroupFields = group.getSelectGroupFields();

        // 查询一级分组时才考虑分页情况
        if (!Boolean.TRUE.equals(group.getNeedPagination()) || CollectionUtils.isNotEmpty(selectGroupFields)) {
            page.setCurrentPage(1);
            page.setSize(count);
        }

        if (!isFetchData) {
            return queryGroupInfo(group);
        } else {

        }

        return null;
    }

    private <T> GroupResult fetchAllData(Grouping group, Pagination<T> page, IWrapper<T> wrapper) {
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

    private GroupResult queryGroupInfo(final Grouping group) {
        return new GroupResult();
    }


    /**
     * 构建所有已选查询分组的查询条件
     */
    private void appendGroupPageWhereCondition(final Grouping group, QueryWrapper<?> queryWrapper) {
        List<GroupSelectField> selectGroupFields = group.getSelectGroupFields();
        if (CollectionUtils.isEmpty(selectGroupFields)) {
            return;
        }
        queryWrapper.and(andWrapper -> {
            for (GroupSelectField selectGroupField : selectGroupFields) {
                appendGroupPageWhereCondition(group, andWrapper, selectGroupField, new ArrayList<>());
            }
        });
    }

    private void appendGroupPageWhereCondition(
            Grouping group,
            QueryWrapper<?> groupCondition,
            GroupSelectField currentSelectField, List<Pair<String, String>> groupColumnValues
    ) {
        if (CollectionUtils.isEmpty(currentSelectField.getChildGroupSelectFields())) {
            groupCondition.or(orWrapper -> {
                for (Pair<String, String> groupColumnValue : groupColumnValues) {
                    orWrapper.eq(groupColumnValue.getLeft(), groupColumnValue.getRight());
                }
            });
            return;
        }

        GroupField groupField = currentSelectField.getGroupField();
        String field = groupField.getField();
        ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(field);
        if (modelFieldConfig == null) {
            throw PamirsException.construct(GroupingExpEnumerate.FIELD_NOT_FIND).appendMsg("模型" + group.getModel() + "字段" + field + "找不到").errThrow();
        }

        groupColumnValues.add(Pair.of(modelFieldConfig.getColumn(), currentSelectField.getGroupValue()));
        for (GroupSelectField childGroupSelectField : currentSelectField.getChildGroupSelectFields()) {
            appendGroupPageWhereCondition(group, groupCondition, childGroupSelectField, groupColumnValues);
        }
        groupColumnValues.remove(groupColumnValues.size() - 1);
    }

    private QueryWrapper<?> buildPageQueryWrapper(Grouping group) {
        QueryWrapper<?> queryWrapper = new QueryWrapper<>();
        queryWrapper.from(group.getModel())
                .setRsql(group.getPageRsql())
                .setQueryData(JsonUtils.parseObject(group.getPageQueryData(), QUERY_DATA_TYPE_REF));
        return queryWrapper;
    }

}
