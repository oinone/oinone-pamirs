package pro.shushi.pamirs.grouping.query.grouping;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.runtime.executor.FieldPermissionExecutor;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.GroupingDataWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.enumeration.GroupingExpEnumerate;
import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.utils.TableGroupingDataHelper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationReadApi;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 快速表格分组查询
 *
 * @author Adamancy Zhang at 16:52 on 2025-11-14
 */
@Order(0)
@Component
public class TableGroupingFastQuery<T> extends AbstractTableGroupingQuery<T> implements TableGroupingQueryApi<T> {

    @Resource
    private RelationReadApi relationReadApi;

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        return isFastQuery(context) || isOnlySingleGrouping(context);
    }

    @Override
    public void queryGroupingPage(TableGroupingQueryContext<T> context, TableGroupingResult result) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        Pagination<T> pagination = context.getPagination();
        if (isFastQuery(context)) {
            QueryWrapper<T> queryWrapper = context.generatorQueryWrapperWithOrderBy();
            String model = queryWrapper.getModel();
            // FIXME: zbh 20251117 此处需使用 queryPage 查询数据
            List<T> list = Models.origin().queryListByWrapper(queryWrapper);
            listFieldQuery(context, list);
            Map<String, GroupingDataWrapper> groupingDataWrapperMap = TableGroupingDataHelper.generatorGroupingDataList(queryList, list);
            FieldPermissionExecutor.filter(model, list);
            result.setGroups(TableGroupingDataHelper.collectionGroupingData(model, groupingDataWrapperMap, queryList));
        } else {
            result.setGroups(TableGroupingDataHelper.collectionGroupingData(context.getModel(), queryFirstGroupingDataMap(context, pagination, true), queryList));
        }
        computePaging(pagination, result);
    }

    private boolean isFastQuery(TableGroupingQueryContext<T> context) {
        return context.getTotalElements().compareTo(200L) <= 0;
    }

    private boolean isOnlySingleGrouping(TableGroupingQueryContext<T> context) {
        return context.getQueryList().size() == 1;
    }

    private void listFieldQuery(TableGroupingQueryContext<T> context, List<T> list) {
        List<String> relationFields = context.getQueryRelationFields();
        if (CollectionUtils.isEmpty(relationFields)) {
            return;
        }
        String model = context.getModel();
        for (String relationField : relationFields) {
            ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, relationField);
            if (modelFieldConfig == null) {
                throw PamirsException.construct(GroupingExpEnumerate.QUERY_RELATION_FIELD_IS_NOT_FOUND, relationField).errThrow();
            }
            List<Object> relationQueryDataList = new ArrayList<>();
            for (Object item : list) {
                if (relationReadApi.isNeedQueryRelation(modelFieldConfig, item)) {
                    relationQueryDataList.add(item);
                }
            }
            if (!relationQueryDataList.isEmpty()) {
                String field = modelFieldConfig.getField();
                DataShardingHelper.build().sharding(relationQueryDataList, (sublist) -> Models.origin().listFieldQuery(sublist, field));
            }
        }
    }
}
