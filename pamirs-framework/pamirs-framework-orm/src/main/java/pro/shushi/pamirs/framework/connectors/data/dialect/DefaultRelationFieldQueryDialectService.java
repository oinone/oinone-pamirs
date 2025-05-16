package pro.shushi.pamirs.framework.connectors.data.dialect;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.api.utils.SortUtils;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.framework.orm.helper.QueryFieldColumnsHelper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.orm.tpl.ValuesTemplate;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.common.util.MapUtils;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.*;

/**
 * 默认关联字段查询方言服务
 *
 * @author Adamancy Zhang at 18:32 on 2024-10-18
 */
@Order
@Dialect.component
@Component
@SPI.Service
public class DefaultRelationFieldQueryDialectService extends AbstractRelationFieldQueryDialectService implements RelationFieldQueryDialectService {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected <T> List<T> listFieldQueryByRelation(ModelFieldConfig modelFieldConfig, List<T> dataList, FillQueryData consumer) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }

        QueryWrapper queryWrapper = generateRelationQuery(modelFieldConfig, dataList);
        if (null == queryWrapper) {
            return dataList;
        }
        List<Object> resultList = queryOneToManyByWrapper(modelFieldConfig, queryWrapper, false);
        if (CollectionUtils.isEmpty(resultList)) {
            return dataList;
        }

        Map<String, Object> throughMap = null;
        if (TtypeEnum.M2M.value().equals(modelFieldConfig.getTtype())) {
            throughMap = new HashMap<>(resultList.size());
            for (Object result : resultList) {
                String relationThrough = generateFieldValuesKey(modelFieldConfig.getThrough(),
                        modelFieldConfig.getThroughRelationFields(), result);
                String referenceThrough = generateFieldValuesKey(modelFieldConfig.getThrough(),
                        modelFieldConfig.getThroughReferenceFields(), result);
                ((List<Object>) MapUtils.computeIfAbsent(throughMap, relationThrough, k -> new ArrayList<>())).add(referenceThrough);
            }
            List<String> throughReferenceFields = modelFieldConfig.getThroughReferenceFields();
            String references = modelFieldConfig.getReferences();
            List<String> referenceFields = modelFieldConfig.getReferenceFields();
            QueryWrapper throughQuery = generateRelationQuery(modelFieldConfig,
                    modelFieldConfig.getThrough(), throughReferenceFields, references, referenceFields, resultList);
            resultList = Models.data().queryListByWrapper(throughQuery);
        }

        consumer.fill(modelFieldConfig, dataList, resultList, throughMap);
        return dataList;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> Object queryFieldByRelation(ModelFieldConfig modelFieldConfig, T data) {
        if (TtypeEnum.M2O.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.O2O.value().equals(modelFieldConfig.getTtype())) {
            QueryWrapper queryWrapper = generateRelationQuery(modelFieldConfig, data);
            if (null == queryWrapper) {
                return null;
            }
            return Models.data().queryOneByWrapper(queryWrapper);
        } else {
            QueryWrapper queryWrapper = generateRelationQuery(modelFieldConfig, data);
            List<Object> dataList = null == queryWrapper ? makeEmptyList() : queryOneToManyByWrapper(modelFieldConfig, queryWrapper);

            if (TtypeEnum.O2M.value().equals(modelFieldConfig.getTtype())) {
                return dataList;
            } else if (TtypeEnum.M2M.value().equals(modelFieldConfig.getTtype())) {
                if (null == dataList) {
                    return makeEmptyList();
                } else if (0 == dataList.size()) {
                    return dataList;
                }
                QueryWrapper throughQuery = generateRelationQuery(modelFieldConfig,
                        modelFieldConfig.getThrough(), modelFieldConfig.getThroughReferenceFields(),
                        modelFieldConfig.getReferences(), modelFieldConfig.getReferenceFields(), dataList);
                return null == throughQuery ? makeEmptyList() : Models.data().queryListByWrapper(throughQuery);
            } else {
                return unSupportTtype(modelFieldConfig);
            }
        }
    }

    @Override
    public <T, R> QueryWrapper<R> generateRelationQuery(ModelFieldConfig modelFieldConfig, T data) {
        String relationModel = modelFieldConfig.getModel();
        List<String> relationFields = modelFieldConfig.getRelationFields();
        List<String> queryFields = null;
        String queryModel = null;
        if (TtypeEnum.M2O.value().equals(modelFieldConfig.getTtype())
                || TtypeEnum.O2O.value().equals(modelFieldConfig.getTtype())
                || TtypeEnum.O2M.value().equals(modelFieldConfig.getTtype())
        ) {
            queryFields = modelFieldConfig.getReferenceFields();
            queryModel = modelFieldConfig.getReferences();
        } else if (TtypeEnum.M2M.value().equals(modelFieldConfig.getTtype())) {
            queryFields = modelFieldConfig.getThroughRelationFields();
            queryModel = modelFieldConfig.getThrough();
        } else {
            unSupportTtype(modelFieldConfig);
        }
        return generateRelationQuery(modelFieldConfig, relationModel, relationFields, queryModel, queryFields, data);
    }

    @Override
    public <T, R> List<R> queryOneToManyByRelation(ModelFieldConfig modelFieldConfig, T data) {
        QueryWrapper<R> queryWrapper = generateRelationQuery(modelFieldConfig, data);
        return queryOneToManyByWrapper(modelFieldConfig, queryWrapper);
    }

    private <R> List<R> queryOneToManyByWrapper(ModelFieldConfig modelFieldConfig, QueryWrapper<R> queryWrapper) {
        return queryOneToManyByWrapper(modelFieldConfig, queryWrapper, true);
    }

    private <R> List<R> queryOneToManyByWrapper(ModelFieldConfig modelFieldConfig, QueryWrapper<R> queryWrapper, boolean page) {
        if (null == queryWrapper) {
            return makeEmptyList();
        }
        if (PamirsSession.directive().isFromClient()) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Pagination<R> pageCondition = new Pagination().setModel(queryWrapper.getModel());
            pageCondition.setSize(page ? modelFieldConfig.getPageSize() : -1);
            pageCondition.setSort(SortUtils.sort(modelFieldConfig.getOrdering()));
            return Models.data().queryListByWrapper(pageCondition, queryWrapper);
        } else {
            return Models.data().queryListByWrapper(queryWrapper);
        }
    }

    @SuppressWarnings("unchecked")
    private <T, R> QueryWrapper<R> generateRelationQuery(ModelFieldConfig relRefField,
                                                         String relationModel, List<String> relationFields,
                                                         String queryModel, List<String> queryFields,
                                                         T data) {
        if (CollectionUtils.isEmpty(relationFields) || CollectionUtils.isEmpty(queryFields)
                || relationFields.size() != queryFields.size()
                || null == queryModel) {
            throw PamirsException.construct(OrmExpEnumerate.BASE_RELATION_CONFIG_ERROR)
                    .appendMsg("model:" + relRefField.getModel() + ",field:" + relRefField.getField()).errThrow();
        }

        List<String> queryColumns = QueryFieldColumnsHelper.fetchQueryFieldColumns(relRefField, queryModel, queryFields);
        QueryWrapper<R> queryWrapper;
        Map<String, ModelFieldConfig> modelFieldConfigMap = new HashMap<>(relationFields.size());
        for (String relationField : relationFields) {
            ModelFieldConfig relationModelField = getModelFieldConfig(relationModel, relationField);
            modelFieldConfigMap.put(relationField, relationModelField);
        }
        int valuesSize = sizeObjOrList(data);
        if (1 == relationFields.size()) {
            ValuesTemplate<List<Object>, Set<Object>, List<Object>> template = ValuesTemplate.getInstance(valuesSize);
            template.supplier(() -> new ArrayList<>(valuesSize), () -> new LinkedHashSet<>(valuesSize));
            consumeObjOrList(data, (item, index) -> {
                for (String relationField : relationFields) {
                    Object fieldValue = fetchFieldValue(modelFieldConfigMap.get(relationField), item);
                    template.consume(v -> v.add(fieldValue), v -> v.add(fieldValue));
                }
            });
            List<Object> queryValues = template.provide(v -> v, ArrayList::new);
            queryWrapper = generateRelationQuery0(queryModel, queryColumns.get(0), queryValues);
        } else {
            ValuesTemplate<List<Object>[], LinkedHashMap<String, List<Object>>, List<Object>[]> template =
                    ValuesTemplate.getInstance(valuesSize);
            template.supplier(() -> new ArrayList[relationFields.size()], () -> new LinkedHashMap<>(relationFields.size()));
            consumeObjOrList(data, (item, index) -> {
                int i = 0;
                for (String relationField : relationFields) {
                    String key = queryColumns.get(i) + CharacterConstants.SEPARATOR_OCTOTHORPE + relationField;
                    Object fieldValue = fetchFieldValue(modelFieldConfigMap.get(relationField), item);
                    final int j = i;
                    template.consume(v -> {
                        if (null == v[j]) {
                            v[j] = new ArrayList<>(valuesSize);
                        }
                        v[j].add(fieldValue);
                    }, v -> MapUtils.computeIfAbsent(v, key, k -> new ArrayList<>(valuesSize)).add(fieldValue));
                    i++;
                }
            });
            List<Object>[] queryValuesArray = template.provide(d -> d, d -> ListUtils.toArray(List.class, d.values()));
            queryWrapper = generateRelationQuery0(queryModel, queryColumns, queryValuesArray);
        }
        return queryWrapper;
    }
}
