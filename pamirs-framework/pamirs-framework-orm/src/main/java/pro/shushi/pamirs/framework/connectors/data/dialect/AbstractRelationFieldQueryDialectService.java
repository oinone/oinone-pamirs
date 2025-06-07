package pro.shushi.pamirs.framework.connectors.data.dialect;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.holder.RelationFieldQueryDialectServiceHolder;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.orm.converter.entity.type.PersistenceEnumConverter;
import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsMapperConfigurationProxy;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.orm.tpl.ValuesTemplate;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.relation.RelationKey;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.bit.SessionMetaBit;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.common.util.MapUtils;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.framework.common.constants.ConfigureConstants.PAMIRS_FRAMEWORK_GATEWAY_CONFIG_PREFIX;

/**
 * 抽象关联字段查询方言服务
 *
 * @author Adamancy Zhang at 18:50 on 2024-10-18
 */
public abstract class AbstractRelationFieldQueryDialectService implements RelationFieldQueryDialectService {

    @Resource
    protected PamirsMapperConfigurationProxy pamirsMapperConfigurationProxy;

    @Value("${" + PAMIRS_FRAMEWORK_GATEWAY_CONFIG_PREFIX + ".async:false}")
    protected boolean pamirsFrameworkGatewayAsync;

    @Resource
    protected PersistenceEnumConverter persistenceEnumConverter;

    @Override
    public <T> List<T> listFieldQueryByRelation(ModelFieldConfig modelFieldConfig, List<T> dataList) {
        return listFieldQueryByRelation(modelFieldConfig, dataList, this::fillQueryData);
    }

    @Override
    public List<Object> listFieldQueryByRelationKey(List<String> keyList, Map<Object, Object> keyContexts) {
        return listFieldQueryByRelationKey(keyList, keyContexts, null);
    }

    @Override
    public List<Object> listFieldQueryByRelationKey(List<String> keyList, Map<Object, Object> keyContexts, BiFunction<ModelFieldConfig, Object, Object> resultHandler) {
        Map<String/*model#field*/, ModelFieldConfig> fieldConfigMap = new HashMap<>();
        Map<String/*model#field*/, List<Object>> relationKeyMap = new HashMap<>();
        for (String key : keyList) {
            RelationKey relationKey = (RelationKey) keyContexts.get(key);
            String modelAndField = relationKey.getModelAndField();
            fieldConfigMap.put(modelAndField, relationKey.getModelFieldConfig());
            MapUtils.computeIfAbsent(relationKeyMap, modelAndField, k -> new ArrayList<>()).add(relationKey.getSource());
        }
        List<CompletableFuture<List<Map<String, Object>>>> queryFuture = relationKeyMap.keySet().stream()
                .map(modelAndField -> {
                    if (pamirsFrameworkGatewayAsync) {
                        return CompletableFuture.supplyAsync(() -> listFieldQueryByRelationKey(fieldConfigMap, relationKeyMap, modelAndField, resultHandler), RelationFieldQueryDialectServiceHolder.getExecutorService());
                    } else {
                        return CompletableFuture.completedFuture(listFieldQueryByRelationKey(fieldConfigMap, relationKeyMap, modelAndField, resultHandler));
                    }
                }).collect(Collectors.toList());

        List<Map<String, Object>> dataList = queryFuture.stream()
                .map(CompletableFuture::join) //join waiting all task completed
                .reduce((all, list) -> {
                    all.addAll(list);
                    return all;
                }).orElse(null);

        Map<String, Object> dataMap = new HashMap<>();
        if (null != dataList) {
            for (Map<String, Object> result : dataList) {
                String dataName = (String) result.get(FieldConstants.RELATION_NAME);
                dataMap.put((String) result.get(FieldConstants.RELATION_CODE), result.get(dataName));
            }
        }
        List<Object> resultList = new ArrayList<>();
        for (String key : keyList) {
            RelationKey relationKey = (RelationKey) keyContexts.get(key);
            resultList.add(dataMap.get(relationKey.getKey()));
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    protected List<Map<String, Object>> listFieldQueryByRelationKey(Map<String/*model#field*/, ModelFieldConfig> fieldConfigMap,
                                                                    Map<String/*model#field*/, List<Object>> relationKeyMap,
                                                                    String modelAndField,
                                                                    BiFunction<ModelFieldConfig, Object, Object> resultHandler) {
        Long directive = Optional.ofNullable(PamirsSession.directive()).map(SessionMetaBit::bitValue).orElse(0L);
        try {
            PamirsSession.directive().initMetaBit(directive);
            List<Map<String, Object>> queryDataList = new ArrayList<>();
            ModelFieldConfig modelFieldConfig = fieldConfigMap.get(modelAndField);
            String model = modelFieldConfig.getModel();
            List<String> relationFields = modelFieldConfig.getRelationFields();
            List<Object> sources = relationKeyMap.get(modelAndField);
            if (!CollectionUtils.isEmpty(sources)) {
                for (Object source : sources) {
                    Map<String, Object> data = new HashMap<>();
                    data.put(FieldConstants.RELATION_NAME, modelFieldConfig.getLname());
                    String key = RelationKey.key(modelFieldConfig, source);
                    data.put(FieldConstants.RELATION_CODE, key);
                    queryDataList.add(Models.compute().copyFieldValues(source, data, model, relationFields));
                }
            }
            ModelFieldConfig fieldConfig = fieldConfigMap.get(modelAndField);
            List<Map<String, Object>> resultList = listFieldQueryByRelation(fieldConfig, queryDataList);
            if (null != resultHandler) {
                return (List<Map<String, Object>>) resultHandler.apply(fieldConfig, resultList);
            }
            return resultList;
        } finally {
            PamirsSession.clearSubSession();
        }
    }

    @SuppressWarnings("rawtypes")
    protected abstract <T> List<T> listFieldQueryByRelation(ModelFieldConfig modelFieldConfig, List<T> dataList, FillQueryData consumer);

    protected <R> QueryWrapper<R> generateRelationQuery0(String queryModel, List<String> columns, List<Object>[] fieldValues) {
        if (pro.shushi.pamirs.meta.common.util.ArrayUtils.hasNullItem(fieldValues)) {
            return null;
        }
        QueryWrapper<R> queryWrapper = Pops.query();
        if (1 == fieldValues[0].size()) {
            int i = 0;
            for (String column : columns) {
                List<Object> values = fieldValues[i];
                Object fieldValue = values.get(0);
                QueryWrapper<R> genResult = generateWrapperEqOrIn(queryWrapper, column, fieldValue);
                if (null == genResult) {
                    return null;
                }
                i++;
            }
        } else {
            queryWrapper.in(columns, fieldValues);
        }
        queryWrapper.setModel(queryModel);
        queryWrapper.setBatchSize(fetchReadBatchSize(queryModel));
        return queryWrapper;
    }

    protected <R> QueryWrapper<R> generateRelationQuery0(String queryModel, String column, List<Object> fieldValues) {
        QueryWrapper<R> queryWrapper;
        fieldValues = ListUtils.filterNullItemForList(fieldValues);
        if (CollectionUtils.isEmpty(fieldValues)) {
            return null;
        }
        queryWrapper = Pops.query();
        if (1 == fieldValues.size()) {
            Object fieldValue = fieldValues.get(0);
            QueryWrapper<R> genResult = generateWrapperEqOrIn(queryWrapper, column, fieldValue);
            if (null == genResult) {
                return null;
            }
        } else {
            queryWrapper.in(column, fieldValues);
        }
        queryWrapper.setModel(queryModel);
        queryWrapper.setBatchSize(fetchReadBatchSize(queryModel));
        return queryWrapper;
    }

    protected <R> QueryWrapper<R> generateWrapperEqOrIn(QueryWrapper<R> queryWrapper,
                                                        String column, Object fieldValue) {
        if (null != fieldValue) {
            if (TypeUtils.isCollection(fieldValue.getClass())) {
                Collection<?> fvs = ListUtils.filterNullItemForCollection((Collection<?>) fieldValue);
                if (CollectionUtils.isEmpty(fvs)) {
                    return null;
                }
                if (1 == fvs.size()) {
                    queryWrapper.eq(column, fvs.iterator().next());
                } else {
                    queryWrapper.in(column, fvs);
                }
            } else if (fieldValue.getClass().isArray()) {
                Object[] fvs = pro.shushi.pamirs.meta.common.util.ArrayUtils.filterNullItemForArray((Object[]) fieldValue);
                if (ArrayUtils.isEmpty(fvs)) {
                    return null;
                }
                if (1 == fvs.length) {
                    queryWrapper.eq(column, fvs[0]);
                } else {
                    queryWrapper.in(column, fvs);
                }
            } else {
                queryWrapper.eq(column, fieldValue);
            }
        } else {
            return null;
        }
        return queryWrapper;
    }

    protected <T> void fillQueryData(ModelFieldConfig modelFieldConfig, List<T> dataList, List<Object> resultList,
                                     Map<String, Object> throughMap) {
        String relationModel = modelFieldConfig.getModel();
        List<String> relationFields = modelFieldConfig.getRelationFields();
        String queryModel = modelFieldConfig.getReferences();
        List<String> queryFields = modelFieldConfig.getReferenceFields();

        int valuesSize = TtypeEnum.isRelationOne(modelFieldConfig.getTtype()) ? 1 : -1;
        ValuesTemplate<Map<Object, Object>, Map<String, List<Object>>, Object> template = ValuesTemplate.getInstance(valuesSize);
        template.supplier(HashMap::new, HashMap::new);
        for (Object item : resultList) {
            String key = generateFieldValuesKey(queryModel, queryFields, item);
            template.consume(d -> d.put(key, item), d -> MapUtils.computeIfAbsent(d, key, k -> new ArrayList<>()).add(item));
        }
        boolean isM2m = TtypeEnum.M2M.value().equals(modelFieldConfig.getTtype());
        for (Object data : dataList) {
            Object key = generateFieldValuesKey(relationModel, relationFields, data);
            if (isM2m) {
                key = throughMap.get(key);
            }
            if (null == key) {
                continue;
            }
            final Object finalKey = key;
            Object value = template.provide(d -> d.get(finalKey), d -> isM2m ? listKeyData(finalKey, d) : d.get(finalKey));
            FieldUtils.setFieldValue(data, modelFieldConfig.getLname(), value);
        }
    }

    @SuppressWarnings("unchecked")
    protected Object listKeyData(Object keys, Map<String, List<Object>> data) {
        List<Object> resultList = new ArrayList<>();
        for (String key : (List<String>) keys) {
            List<Object> value = data.get(key);
            if (null != value) {
                resultList.addAll(value);
            }
        }
        return resultList;
    }

    protected Object unSupportTtype(ModelFieldConfig modelFieldConfig) {
        throw PamirsException.construct(OrmExpEnumerate.BASE_TTYPE_IS_NOT_SUPPORT_ERROR)
                .appendMsg("model:" + modelFieldConfig.getModel() + ",field:" + modelFieldConfig.getField()
                        + ",ttype:" + modelFieldConfig.getTtype()).errThrow();
    }

    protected ModelFieldConfig getModelFieldConfig(String relationModel, String relationField) {
        ModelFieldConfig relationModelField;
        if (FieldUtils.isConstantRelationFieldValue(relationField)) {
            relationModelField = new ModelFieldConfig().setDefaultValue(relationField.substring(1, relationField.length() - 1));
        } else {
            relationModelField = PamirsSession.getContext().getModelField(relationModel, relationField);
        }
        return relationModelField;
    }

    protected <T> Object fetchFieldValue(ModelFieldConfig relationModelField, T data) {
        Object relationFieldValue;
        if (null == relationModelField.getField()) {
            relationFieldValue = relationModelField.getDefaultValue();
        } else {
            String lname = relationModelField.getLname();
            relationFieldValue = FieldUtils.getFieldValue(data, lname);
            if (TtypeEnum.ENUM.value().equals(relationModelField.getTtype())) {
                Map<String, Object> map = new HashMap<>();
                map.put(lname, relationFieldValue);
                persistenceEnumConverter.in(relationModelField, map);
                relationFieldValue = map.get(lname);
            }
        }
        return relationFieldValue;
    }

    protected String/*value1#value2#...*/ generateFieldValuesKey(String model, List<String> uniqueFields, Object obj) {
        List<Object> values = new ArrayList<>();
        for (String uniqueField : uniqueFields) {
            ModelFieldConfig fieldConfig = getModelFieldConfig(model, uniqueField);
            Object v = fetchFieldValue(fieldConfig, obj);
            // 为了支持关系字段使用#常量#,并且对应的是枚举字段的方式,此处将枚举字段都处理成value
            if (TtypeEnum.ENUM.value().equals(fieldConfig.getTtype())) {
                Map<String, Object> map = new HashMap<>();
                map.put(fieldConfig.getLname(), v);
                persistenceEnumConverter.in(fieldConfig, map);
                v = map.get(fieldConfig.getLname());
            }
            values.add(v);
        }
        return StringUtils.join(values, ModelComputeApi.VALUES_SEPARATOR);
    }

    protected <T> int sizeObjOrList(T data) {
        if (data instanceof List) {
            return ((List<?>) data).size();
        } else {
            return 1;
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> void consumeObjOrList(T data, BiConsumer<Object, Integer> consumer) {
        if (data instanceof List) {
            int i = 0;
            for (Object item : (List<Object>) data) {
                consumer.accept(item, i);
                i++;
            }
        } else {
            consumer.accept(data, 0);
        }
    }

    protected Integer fetchReadBatchSize(String model) {
        // @see pro.shushi.pamirs.framework.connectors.data.api.orm.BatchSizeHintApi
        Integer batchSize = PamirsSession.getBatchSize();
        if (batchSize == null) {
            batchSize = pamirsMapperConfigurationProxy.batchOperationForModel(model).getRead();
        }
        return batchSize;
    }

    protected <R> List<R> makeEmptyList() {
        return new ArrayList<>();
    }

    protected interface FillQueryData<T> {
        void fill(ModelFieldConfig modelFieldConfig, List<T> returnList, List<Object> dataList, Map<String, String> throughMap);
    }
}
