package pro.shushi.pamirs.grouping.query.grouping;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.query.GQLFieldsQuery;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.grouping.utils.TableGroupingHelper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientFieldConverter;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelatedFieldQueryApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationReadApi;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.model.RelatedValue;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.FieldUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 全量查询分组及分组数据
 * <ul>
 *     <li>1. 表格分组不超过配置值时全部展开</li>
 *     <li>2. 表格数据不分页</li>
 * </ul>
 *
 * @author Adamancy Zhang at 16:52 on 2025-11-14
 */
@Order(0)
@Component
public class TableGroupingFullQuery<T> implements TableGroupingQueryApi<T> {

    @Resource
    private RelationReadApi relationReadApi;

    @Resource
    private RelatedFieldQueryApi relatedFieldQueryApi;

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        long size = context.getPagination().getSize();
        if (size < 0) {
            return true;
        }
        return context.getTotalElements().compareTo(200L) <= 0;
    }

    @Override
    public void queryGroupingPage(TableGroupingQueryContext<T> context, TableGroupingResult result) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        Pagination<T> pagination = context.getPagination();
        List<T> list = fetchFullList(context, context.generatorQueryWrapperWithOrderBy());
        result.setGroups(TableGroupingHelper.fullDataConvertGroups(queryList, context.getModel(), list, true));
        TableGroupingHelper.computePaging(pagination, result);
    }

    @SuppressWarnings("unchecked")
    private List<T> fetchFullList(TableGroupingQueryContext<T> context, QueryWrapper<T> queryWrapper) {
        GQLFieldsQuery gqlFieldsQuery = context.getGqlFieldsQuery();
        String model = context.getModel();
        List<String> columns = gqlFieldsQuery.getColumns(model);
        if (CollectionUtils.isNotEmpty(columns)) {
            queryWrapper.select(columns.toArray(new String[0]));
        }
        List<T> list = Models.origin().queryListByWrapper(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        fillData((List<Object>) list, gqlFieldsQuery, model, model);
        return list;
    }

    private void fillData(List<Object> list, GQLFieldsQuery gqlFieldsQuery, String model, String key) {
        List<String> relationFields = gqlFieldsQuery.getRelationFields(key);
        if (CollectionUtils.isNotEmpty(relationFields)) {
            for (String relationField : relationFields) {
                listFieldQuery(list, gqlFieldsQuery, model, relationField, model);
            }
        }
        List<String> relatedFields = gqlFieldsQuery.getRelatedFields(key);
        if (CollectionUtils.isNotEmpty(relatedFields)) {
            for (String relatedField : relatedFields) {
                listRelatedFieldQuery(list, model, relatedField);
            }
        }
    }

    private void listFieldQuery(List<Object> list, GQLFieldsQuery gqlFieldsQuery, String model, String field, String key) {
        ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, field);
        List<Object> relationQueryDataList = new ArrayList<>();
        for (Object item : list) {
            Object fieldValue = FieldUtils.getFieldValue(item, modelFieldConfig.getLname());
            if (relationReadApi.isNeedQueryRelation(modelFieldConfig, fieldValue)) {
                relationQueryDataList.add(item);
            }
        }
        if (relationQueryDataList.isEmpty()) {
            return;
        }
        Models.directive().request(() -> relationReadApi.listFieldQueryByRelation(modelFieldConfig, relationQueryDataList));
        String nextKey = key + CharacterConstants.SEPARATOR_OCTOTHORPE + field;
        String references = modelFieldConfig.getReferences();
        String lname = modelFieldConfig.getLname();
        List<Object> nextQueryDataList = new ArrayList<>();
        for (Object relationQueryData : relationQueryDataList) {
            Object target = FieldUtils.getFieldValue(relationQueryData, lname);
            if (target == null) {
                continue;
            }
            if (target instanceof Collection) {
                nextQueryDataList.addAll((Collection<?>) target);
            } else {
                nextQueryDataList.add(target);
            }
        }
        if (!nextQueryDataList.isEmpty()) {
            fillData(nextQueryDataList, gqlFieldsQuery, references, nextKey);
        }
    }

    private void listRelatedFieldQuery(List<Object> list, String model, String field) {
        ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, field);
        String references = modelFieldConfig.getReferences();
        String lname = modelFieldConfig.getLname();
        for (Object item : list) {
            Object value = FieldUtils.getFieldValue(item, lname);
            if (value != null) {
                continue;
            }
            RelatedValue relatedValueResult = Models.directive().request(() -> relatedFieldQueryApi.queryRelated(modelFieldConfig, item));
            Object result = relatedValueResult.getRelatedValue();
            // 前后端字段适配
            if (StringUtils.isBlank(references)) {
                result = Spider.getDefaultExtension(ClientFieldConverter.class).out(modelFieldConfig, result);
            } else {
                result = ClientDataConverter.get().out(references, result);
            }
            FieldUtils.setFieldValue(item, lname, result);
        }
    }
}
