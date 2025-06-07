package pro.shushi.pamirs.framework.connectors.data.dialect.mssql;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.api.utils.SortUtils;
import pro.shushi.pamirs.framework.connectors.data.dialect.AbstractRelationFieldQueryDialectService;
import pro.shushi.pamirs.framework.connectors.data.dialect.RelationFieldQueryDialectService;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.framework.orm.helper.QueryFieldColumnsHelper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.orm.tpl.ValuesTemplate;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.common.util.MapUtils;
import pro.shushi.pamirs.meta.enmu.DataSourceEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.*;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.framework.connectors.data.dialect.constants.DataProductVersion.DEFAULT_SQLSERVER_MAJOR_VERSION;
import static pro.shushi.pamirs.framework.connectors.data.dialect.constants.DataProductVersion.DEFAULT_SQLSERVER_VERSION;

/**
 * SQLServer关联字段查询方言服务
 *
 * @author Adamancy Zhang at 18:35 on 2024-10-18
 */
@Dialect.component(type = DataSourceEnum.SQL_SERVER, version = DEFAULT_SQLSERVER_VERSION, majorVersion = DEFAULT_SQLSERVER_MAJOR_VERSION)
@Component
public class SqlServerRelationFieldQueryDialectService extends AbstractRelationFieldQueryDialectService implements RelationFieldQueryDialectService {

    @Override
    public <T> Object queryFieldByRelation(ModelFieldConfig modelFieldConfig, T data) {
        if (TtypeEnum.M2O.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.O2O.value().equals(modelFieldConfig.getTtype())) {
            List<QueryWrapper<T>> queryWrapper = generateRelationQuery0(modelFieldConfig, data);
            if (CollectionUtils.isEmpty(queryWrapper)) {
                return null;
            }
            return Models.data().queryOneByWrapper(queryWrapper.get(0));
        } else {
            List<QueryWrapper<Object>> queryWrapper = generateRelationQuery0(modelFieldConfig, data);
            List<Object> dataList = CollectionUtils.isEmpty(queryWrapper) ? makeEmptyList() : this.queryOneToManyByWrapper(modelFieldConfig, queryWrapper);

            if (TtypeEnum.O2M.value().equals(modelFieldConfig.getTtype())) {
                return dataList;
            } else if (TtypeEnum.M2M.value().equals(modelFieldConfig.getTtype())) {
                if (CollectionUtils.isEmpty(dataList)) {
                    return makeEmptyList();
                } else if (0 == dataList.size()) {
                    return dataList;
                }
                List<QueryWrapper<T>> throughQuery = generateRelationQuery(modelFieldConfig,
                        modelFieldConfig.getThrough(), modelFieldConfig.getThroughReferenceFields(),
                        modelFieldConfig.getReferences(), modelFieldConfig.getReferenceFields(), dataList);

                List<T> resultList = throughQuery.stream()
                        .map(queryWrapper1 -> Models.data().queryListByWrapper(queryWrapper1))
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
                return CollectionUtils.isEmpty(throughQuery) ? makeEmptyList() : resultList;
            } else {
                return unSupportTtype(modelFieldConfig);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected <T> List<T> listFieldQueryByRelation(ModelFieldConfig modelFieldConfig, List<T> dataList, FillQueryData consumer) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }

        List<QueryWrapper<Object>> queryWrapper = generateRelationQuery0(modelFieldConfig, dataList);
        if (CollectionUtils.isEmpty(queryWrapper)) {
            return dataList;
        }
        List<Object> resultList = this.queryOneToManyByWrapper(modelFieldConfig, queryWrapper, false);
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
            List<QueryWrapper<T>> throughQuery = generateRelationQuery(modelFieldConfig,
                    modelFieldConfig.getThrough(), throughReferenceFields, references, referenceFields, resultList);
            resultList = throughQuery.stream()
                    .map(queryWrapper2 -> Models.data().queryListByWrapper(queryWrapper2))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }

        consumer.fill(modelFieldConfig, dataList, resultList, throughMap);
        return dataList;
    }

    @Override
    public <T, R> List<R> queryOneToManyByRelation(ModelFieldConfig modelFieldConfig, T data) {
        List<QueryWrapper<R>> queryWrappers = generateRelationQuery0(modelFieldConfig, data);
        return queryOneToManyByWrapper(modelFieldConfig, queryWrappers);
    }

    private <R> List<R> queryOneToManyByWrapper(ModelFieldConfig modelFieldConfig, List<QueryWrapper<R>> queryWrapper) {
        return queryOneToManyByWrapper(modelFieldConfig, queryWrapper, true);
    }

    private <R> List<R> queryOneToManyByWrapper(ModelFieldConfig modelFieldConfig, List<QueryWrapper<R>> queryWrapper, boolean page) {
        if (CollectionUtils.isEmpty(queryWrapper)) {
            return makeEmptyList();
        }
        List<R> resultList = new ArrayList<>();
        for (QueryWrapper<R> rQueryWrapper : queryWrapper) {
            if (PamirsSession.directive().isFromClient()) {
                Pagination<R> pageCondition = new Pagination<>();
                pageCondition.setModel(rQueryWrapper.getModel());
                pageCondition.setSize(page ? modelFieldConfig.getPageSize() : -1);
                pageCondition.setSort(SortUtils.sort(modelFieldConfig.getOrdering()));
                resultList.addAll(Models.data().queryListByWrapper(pageCondition, rQueryWrapper));
            } else {
                resultList.addAll(Models.data().queryListByWrapper(rQueryWrapper));
            }
        }
        return resultList;
    }

    public <T, R> List<QueryWrapper<R>> generateRelationQuery0(ModelFieldConfig modelFieldConfig, T data) {
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

    @SuppressWarnings("unchecked")
    private <T, R> List<QueryWrapper<R>> generateRelationQuery(ModelFieldConfig relRefField,
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
        List<QueryWrapper<R>> queryWrappers = new ArrayList<>();

        if (1 == relationFields.size()) {

            int batchSize = 2100;
            // valuesSize + batchSize - 1 / batchSize
            List<T> dataChunks = new ArrayList<>();
            for (int n = 0; n < (valuesSize + batchSize + 1) / batchSize; n++) {
                if (data instanceof List) {
                    List<T> ts = ((List<T>) data).subList(n * batchSize, Math.min((n + 1) * batchSize, valuesSize));
                    if (CollectionUtils.isEmpty(ts)) {
                        break;
                    }
                    dataChunks.add((T) ts);
                } else {
                    dataChunks.add(data);
                }
            }
            if (CollectionUtils.isEmpty(dataChunks)) {
                return queryWrappers;
            }
            for (T chunk : dataChunks) {
                ValuesTemplate<List<Object>, Set<Object>, List<Object>> template = ValuesTemplate.getInstance(valuesSize);
                template.supplier(() -> new ArrayList<>(valuesSize), () -> new LinkedHashSet<>(valuesSize));
                consumeObjOrList(chunk, (item, index) -> {
                    for (String relationField : relationFields) {
                        Object fieldValue = fetchFieldValue(modelFieldConfigMap.get(relationField), item);
                        template.consume(v -> v.add(fieldValue), v -> v.add(fieldValue));
                    }
                });
                List<Object> queryValues = template.provide(v -> v, ArrayList::new);
                queryWrapper = generateRelationQuery0(queryModel, queryColumns.get(0), queryValues);
                if (queryWrapper == null) {
                    break;
                }
                queryWrappers.add(queryWrapper);
            }
        } else {
            int batchSize = 2100 / (relationFields.size() + 1);
            List<T> dataChunks = new ArrayList<>();
            for (int n = 0; n < (valuesSize + batchSize - 1) / batchSize; n++) {
                if (data instanceof List) {
                    List<T> ts = ((List<T>) data).subList(n * batchSize, Math.min((n + 1) * batchSize, valuesSize));
                    if (CollectionUtils.isEmpty(ts)) {
                        break;
                    }
                    dataChunks.add((T) ts);
                } else {
                    dataChunks.add(data);
                }
            }
            if (CollectionUtils.isEmpty(dataChunks)) {
                return queryWrappers;
            }
            for (T chunk : dataChunks) {
                ValuesTemplate<List<Object>[], LinkedHashMap<String, List<Object>>, List<Object>[]> template =
                        ValuesTemplate.getInstance(valuesSize);
                template.supplier(() -> new ArrayList[relationFields.size()], () -> new LinkedHashMap<>(relationFields.size()));
                consumeObjOrList(chunk, (item, index) -> {
                    int i = 0;
                    for (String relationField : relationFields) {
                        Object fieldValue = fetchFieldValue(modelFieldConfigMap.get(relationField), item);
                        final int j = i;
                        template.consume(v -> {
                            if (null == v[j]) {
                                v[j] = new ArrayList<>(valuesSize);
                            }
                            v[j].add(fieldValue);
                        }, v -> MapUtils.computeIfAbsent(v, relationField, k -> new ArrayList<>(valuesSize)).add(fieldValue));
                        i++;
                    }
                });
                List<Object>[] queryValuesArray = template.provide(d -> d, d -> ListUtils.toArray(List.class, d.values()));
                queryWrapper = generateRelationQuery0(queryModel, queryColumns, queryValuesArray);
                if (queryWrapper == null) {
                    break;
                }
                queryWrappers.add(queryWrapper);
            }
        }
        return queryWrappers;
    }

}
