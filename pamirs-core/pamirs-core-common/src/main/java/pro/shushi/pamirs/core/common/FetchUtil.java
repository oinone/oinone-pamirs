package pro.shushi.pamirs.core.common;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.common.api.PamirsBootMainProcessApi;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModulesApi;
import pro.shushi.pamirs.core.common.cache.MemoryIterableSearchCache;
import pro.shushi.pamirs.core.common.cache.UniqueKeyGenerator;
import pro.shushi.pamirs.core.common.enmu.CommonExpEnumerate;
import pro.shushi.pamirs.framework.configure.staticloader.TableInfoFetcher;
import pro.shushi.pamirs.framework.connectors.data.sql.AbstractWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.config.ModelFieldConfigWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.UpdateWrapper;
import pro.shushi.pamirs.framework.faas.hook.builtin.PlaceHolderHook;
import pro.shushi.pamirs.framework.gateways.rsql.PamirsRsqlVisitor;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlQuery;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlSearchOperation;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsMapperConfigurationProxy;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.core.faas.boot.ModulesApi;
import pro.shushi.pamirs.meta.api.core.faas.hook.PlaceHolderParser;
import pro.shushi.pamirs.meta.api.core.orm.ReadApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelatedFieldQueryApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.*;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.base.manager.data.OriginDataManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.ContextConstants;
import pro.shushi.pamirs.meta.constant.SqlConstants;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import jakarta.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class FetchUtil {

    private static final List<String> BASE_FIELD_LIST = CollectionHelper.<String>newInstance()
            .add(VariableNameConstants.entityModel)
            .add(VariableNameConstants.dataModel)
            .build();

    public static final String LIMIT_1 = "LIMIT 1";

    private static final String LANG_KEY = "lang";

    private FetchUtil() {
        //reject create object
    }

    //region cast method

    /**
     * 在已知值类型时可使用此方法进行去类型处理
     *
     * @param value 值
     * @param <R>   已知类型
     * @return 对应类型的值
     */
    @SuppressWarnings("unchecked")
    public static <R> R cast(Object value) {
        return (R) value;
    }

    //endregion

    //region fetch method

    public static <T extends BaseModel> T fetchOne(T data) {
        if (data == null) {
            return null;
        }
        return consumerQueryWrapper(data, data::queryOne, wrapper -> Models.origin().queryOneByWrapper(wrapper));
    }

    public static <T extends BaseModel> T fetchOneOfNullable(T data) {
        return consumerQueryWrapper(data, () -> null, wrapper -> Models.origin().queryOneByWrapper(wrapper));
    }

    public static <T extends BaseModel> Long fetchCount(T data) {
        return consumerQueryWrapper(data, () -> 0L, wrapper -> Models.origin().count(wrapper));
    }

    @NotNull
    public static <T, R> R consumerQueryWrapper(T data, Function<QueryWrapper<T>, R> function) {
        return consumerPkOrUniqueWrapper(data, Pops::query, () -> {
            throw consumerWrapperError();
        }, function);
    }

    public static <T, R> R consumerQueryWrapper(T data, Supplier<R> defaultSupplier, Function<QueryWrapper<T>, R> function) {
        return consumerPkOrUniqueWrapper(data, Pops::query, defaultSupplier, function);
    }

    @NotNull
    public static <T, R> R consumerUpdateWrapper(T data, Function<UpdateWrapper<T>, R> function) {
        return consumerPkOrUniqueWrapper(data, Pops::update, () -> {
            throw consumerWrapperError();
        }, function);
    }

    public static <T, R> R consumerUpdateWrapper(T data, Supplier<R> defaultSupplier, Function<UpdateWrapper<T>, R> function) {
        return consumerPkOrUniqueWrapper(data, Pops::update, defaultSupplier, function);
    }

    public static PamirsException consumerWrapperError() {
        return PamirsException.construct(CommonExpEnumerate.PK_UNIQUE_HAS_NULL).errThrow();
    }

    private static <T, R, W extends AbstractWrapper<T, String, W>> R consumerPkOrUniqueWrapper(T data, Supplier<W> wrapperSupplier, Supplier<R> defaultSupplier, Function<W, R> function) {
        if (data == null) {
            return null;
        }
        String model = Models.api().getModel(data);
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
        MemoryIterableSearchCache<String, ModelFieldConfig> modelFieldConfigCache = new MemoryIterableSearchCache<>(modelConfig.getModelFieldConfigList(), ModelFieldConfig::getName);
        //通过PK查询
        W wrapper = wrapperSupplier.get().from(model);
        boolean isBroken = false;
        for (String pkField : modelConfig.getPk()) {
            pkField = pkField.trim();
            Object pkValue = FieldUtils.getFieldValue(data, pkField);
            if (pkValue == null) {
                isBroken = true;
                break;
            }
            if (pkValue instanceof IEnum) {
                pkValue = ((IEnum<?>) pkValue).value();
            }
            wrapper.eq(ModelFieldConfigWrapper.wrap(modelFieldConfigCache.get(pkField)).getSqlSelect(true), pkValue);
        }
        if (!isBroken) {
            return function.apply(wrapper);
        }
        //通过Uniques查询
        for (String unique : Models.compute().fetchUniqueKeyGroups(modelConfig)) {
            wrapper = wrapperSupplier.get().from(model);
            isBroken = false;
            String[] uniqueFields = unique.split(CharacterConstants.SEPARATOR_COMMA);
            for (String uniqueField : uniqueFields) {
                uniqueField = uniqueField.trim();
                Object uniqueValue = FieldUtils.getFieldValue(data, uniqueField);
                if (uniqueValue == null) {
                    isBroken = true;
                    break;
                }
                if (uniqueValue instanceof IEnum) {
                    uniqueValue = ((IEnum<?>) uniqueValue).value();
                }
                wrapper.eq(ModelFieldConfigWrapper.wrap(modelFieldConfigCache.get(uniqueField)).getSqlSelect(true), uniqueValue);
            }
            if (!isBroken) {
                return function.apply(wrapper);
            }
        }
        return defaultSupplier.get();
    }

    /**
     * 填充全部pk和唯一键
     *
     * @param data   待填充的数据
     * @param origin 具有全部pk和唯一键的值
     * @param <T>    任意对象类型
     */
    public static <T extends D> void fillPkFields(T data, T origin) {
        String model = Models.api().getModel(data);
        consumerPkSet(model, (pkSet, pkColumnSet) -> {
            Map<String, Object> originMap = origin.get_d();
            Map<String, Object> targetMap = data.get_d();
            pkSet.forEach(pk -> {
                Object value = targetMap.get(pk);
                if (value == null) {
                    targetMap.put(pk, originMap.get(pk));
                }
            });
        });
    }

    /**
     * 填充全部pk和唯一键
     *
     * @param data   待填充的数据
     * @param origin 具有全部pk和唯一键的值
     * @param <T>    任意对象类型
     */
    public static <T extends D> void fillPkAndUniqueFields(T data, T origin) {
        String model = Models.api().getModel(data);
        consumerPkAndUniqueSet(model, (pkSet, uniqueSets) -> {
            Map<String, Object> originMap = origin.get_d();
            Map<String, Object> targetMap = data.get_d();
            pkSet.forEach(pk -> {
                Object value = targetMap.get(pk);
                if (value == null) {
                    targetMap.put(pk, originMap.get(pk));
                }
            });
            uniqueSets.forEach(uniqueSet -> {
                uniqueSet.forEach(unique -> {
                    Object value = targetMap.get(unique);
                    if (value == null) {
                        targetMap.put(unique, originMap.get(unique));
                    }
                });
            });
        });
    }

    public static <T extends IdModel> T fetchOneById(String modelModel, Long id) {
        if (null == id) {
            return null;
        }
        return Models.origin().queryOneByWrapper(Pops.<T>lambdaQuery().eq(T::getId, id)
                .setModel(modelModel));
    }

    public static <T extends IdModel> List<T> fetchList(Collection<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        String modelModel = Models.api().getModel(dataList);
        List<Long> ids = new ArrayList<>();
        for (T item : dataList) {
            Long id = item.getId();
            if (id == null) {
                break;
            }
            ids.add(id);
        }
        return Models.origin().queryListByWrapper(Pops.<T>lambdaQuery()
                .from(modelModel)
                .in(IdModel::getId, ids));
    }

    public static <T> List<T> fetchList(List<?> dataList, String modelModel) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        List<Long> ids = new ArrayList<>();
        for (Object item : dataList) {
            Long id = (Long) FieldUtils.getFieldValue(item, SqlConstants.ID);
            if (id == null) {
                break;
            }
            ids.add(id);
        }
        return Models.origin().queryListByWrapper(Pops.<T>query()
                .from(modelModel)
                .in(SqlConstants.ID, ids));
    }

    public static <T extends IdModel> List<T> fetchListByIds(String modelModel, Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return Models.origin().queryListByWrapper(Pops.<T>lambdaQuery().in(T::getId, ids)
                .setModel(modelModel));
    }

    public static <T extends IdModel> List<T> fetchListByIds(Class<T> tClass, Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return Models.origin().queryListByWrapper(Pops.<T>lambdaQuery().in(T::getId, ids)
                .from(tClass));
    }

    public static <T extends IdModel> Map<Long, T> fetchMapByIds(Class<T> tClass, Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new HashMap<>(1);
        }
        List<T> result = fetchListByIds(tClass, ids);
        if (CollectionUtils.isEmpty(result)) {
            return new HashMap<>(1);
        }
        return result.stream().collect(Collectors.toMap(T::getId, Function.identity()));
    }

    public static <T extends CodeModel> Map<String, T> fetchMapByCodes(Class<T> tClass, Collection<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return new HashMap<>(1);
        }
        List<T> result = fetchListByCodes(tClass, codes);
        if (CollectionUtils.isEmpty(result)) {
            return new HashMap<>(1);
        }
        return result.stream().collect(Collectors.toMap(T::getCode, Function.identity()));
    }

    public static <T extends CodeModel> List<T> fetchListByCodes(String modelModel, Collection<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return new ArrayList<>();
        }
        return Models.origin().queryListByWrapper(Pops.<T>lambdaQuery().in(T::getCode, codes)
                .setModel(modelModel));
    }

    public static <T extends CodeModel> List<T> fetchListByCodes(Class<T> tClass, Collection<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return new ArrayList<>();
        }
        List<String> distinctCodes = codes.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(distinctCodes)) {
            return new ArrayList<>();
        }
        return Models.origin().queryListByWrapper(Pops.<T>lambdaQuery().in(T::getCode, distinctCodes)
                .from(tClass));
    }

    public static <T extends IdModel, V> List<T> fetchListBySingleColumn(String modelModel, Getter<T, V> column, List<V> values) {
        List<T> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(values)) {
            return resultList;
        }
        OriginDataManager dataManager = Models.origin();
        int batchSize = 500;
        int i = 0, currentQuerySize = values.size();
        do {
            int k = i + batchSize;
            List<V> targets;
            if (k < currentQuerySize) {
                targets = ListUtils.sub(values, i, k);
            } else {
                targets = ListUtils.sub(values, i, currentQuerySize);
            }
            resultList.addAll(dataManager.queryListByWrapper(Pops.<T>lambdaQuery()
                    .from(modelModel)
                    .in(column, targets)));
            i = k;
        } while (i < currentQuerySize);
        return resultList;
    }

    public static <T extends AbstractModel> String fetchColumn(Getter<T, ?> getter) {
        return PStringUtils.fieldName2Column(LambdaUtil.fetchFieldName(getter));
    }

    public static String replacePlaceholder(String rsql) {
        Map<String, PlaceHolderParser> placeHolderParserMap = PlaceHolderHook.getPlaceHolderParserMap();
        IWrapper<?> wrapper = Pops.query().setRsql(rsql);
        for (String placeHolderParser : placeHolderParserMap.keySet()) {
            placeHolderParserMap.get(placeHolderParser).parse(wrapper);
        }
        return wrapper.getRsql();
    }

    public static String rsqlToSql(String model, String rsql) {
        rsql = replacePlaceholder(rsql);
        Node parse = new RSQLParser(RsqlSearchOperation.getOperators()).parse(rsql);
        RsqlQuery query = parse.accept(new PamirsRsqlVisitor(), PamirsSession.getContext().getSimpleModelConfig(model));
        return query.getWhere().toString();
    }

    public static <T extends AbstractModel> T fetchFirstByEntity(T object) {
        List<T> list = Models.origin().queryListByEntity(object);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    public static <T extends AbstractModel> T fetchFirst(IWrapper<T> wrapper) {
        Pagination<T> pagination = new Pagination<>();
        pagination.setModel(wrapper.getModel())
                .setSize(1L)
                .setCurrentPage(1);
        List<T> list = Models.origin().queryListByWrapper(pagination, wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    public static String fetchScene() {
        return Optional.ofNullable(PamirsSession.getRequestVariables())
                .map(PamirsRequestVariables::getParameterMap)
                .filter(MapUtils::isNotEmpty)
                .map(v -> v.get(ContextConstants.SCENE))
                .filter(v -> v.length >= 1)
                .map(v -> v[0])
                .orElse(null);
    }

    public static String fetchVariables(String key) {
        return Optional.ofNullable(PamirsSession.getRequestVariables())
                .map(PamirsRequestVariables::getVariables)
                .filter(MapUtils::isNotEmpty)
                .map(v -> v.get(key))
                .map(Object::toString)
                .orElse(null);
    }

    public static String fetchLang() {
        return Optional.ofNullable(PamirsSession.getRequestVariables())
                .map(PamirsRequestVariables::getVariables)
                .map(v -> v.get(LANG_KEY))
                .map(StringHelper::valueOf)
                .orElse(null);
    }

    public static <T extends D, R> R safeGetField(T object, Getter<T, R> getter) {
        R result = getter.apply(object);
        if (result == null) {
            String fieldName = LambdaUtil.fetchFieldName(getter);
            String model = Models.api().getModel(object);
            ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, fieldName);
            String ttype = modelFieldConfig.getTtype();
            if (TtypeEnum.isRelationOne(ttype)) {
                List<String> relationFields = modelFieldConfig.getRelationFields(),
                        referenceFields = modelFieldConfig.getReferenceFields();
                if (CollectionUtils.isNotEmpty(relationFields) && CollectionUtils.isNotEmpty(referenceFields)) {
                    String ltype = modelFieldConfig.getLtype();
                    Object m2o;
                    if (TypeUtils.isModelClass(ltype)) {
                        m2o = TypeUtils.getNewInstance(ltype);
                    } else {
                        m2o = new HashMap<>();
                    }
                    int relationFieldLength = relationFields.size();
                    for (int i = 0; i < relationFieldLength; i++) {
                        String relationField = relationFields.get(i),
                                referenceField = referenceFields.get(i);
                        Object relationValue;
                        if (relationField.startsWith(CharacterConstants.SEPARATOR_OCTOTHORPE) && relationField.endsWith(CharacterConstants.SEPARATOR_OCTOTHORPE)) {
                            relationValue = relationField.substring(1, relationField.length() - 1);
                        } else {
                            relationValue = FieldUtils.getFieldValue(object, relationField);
                        }
                        FieldUtils.setFieldValue(m2o, referenceField, relationValue);
                    }
                    FieldUtils.setFieldValue(object, modelFieldConfig.getLname(), m2o);
                    result = getter.apply(object);
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T extends D, R> R safeGetRelatedField(T object, Getter<T, R> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        String model = Models.api().getModel(object);
        return (R) CommonApiFactory.getApi(RelatedFieldQueryApi.class).queryRelated(PamirsSession.getContext().getModelField(model, fieldName), object).getRelatedValue();
    }

    public static void consumerPkSet(String modelModel, BiConsumer<Set<String>, Set<String>> consumer) {
        consumerPkSet0(PamirsSession.getContext().getModelConfig(modelModel), consumer);
    }

    public static void consumerUniqueSet(String modelModel, BiConsumer<List<Set<String>>, List<Set<String>>> consumer) {
        consumerUniqueSet0(PamirsSession.getContext().getModelConfig(modelModel), consumer);
    }

    public static void consumerPkAndUniqueSet(String modelModel, BiConsumer<Set<String>, List<Set<String>>> consumer) {
        consumerPkAndUniqueSet0(PamirsSession.getContext().getModelConfig(modelModel), consumer);
    }

    private static void consumerPkSet0(ModelConfig modelConfig, BiConsumer<Set<String>, Set<String>> consumer) {
        consumerFieldSet(modelConfig.getPk(), consumer);
    }

    private static void consumerUniqueSet0(ModelConfig modelConfig, BiConsumer<List<Set<String>>, List<Set<String>>> consumer) {
        List<Set<String>> uniqueSetList = new ArrayList<>();
        List<Set<String>> uniqueColumnSetList = new ArrayList<>();
        for (String unique : Models.compute().fetchUniqueKeyGroups(modelConfig)) {
            Set<String> uniqueSet = new HashSet<>();
            Set<String> uniqueColumnSet = new HashSet<>();
            String[] uniqueFields = unique.split(CharacterConstants.SEPARATOR_COMMA);
            for (String uniqueField : uniqueFields) {
                uniqueField = uniqueField.trim();
                uniqueSet.add(uniqueField);
                uniqueColumnSet.add(PStringUtils.fieldName2Column(uniqueField));
            }
            uniqueSetList.add(uniqueSet);
            uniqueColumnSetList.add(uniqueColumnSet);
        }
        consumer.accept(uniqueSetList, uniqueColumnSetList);
    }

    private static void consumerPkAndUniqueSet0(ModelConfig modelConfig, BiConsumer<Set<String>, List<Set<String>>> consumer) {
        consumerPkSet0(modelConfig, (pkSet, pkColumnSet) -> consumerUniqueSet0(modelConfig, (uniqueSetList, uniqueColumnSetList) -> consumer.accept(pkSet, uniqueSetList)));
    }

    public static void consumerFieldSet(List<String> fields, BiConsumer<Set<String>, Set<String>> consumer) {
        Set<String> fieldSet = new HashSet<>(fields.size());
        Set<String> fieldColumnSet = new HashSet<>(fields.size());
        for (String field : fields) {
            field = field.trim();
            fieldSet.add(field);
            fieldColumnSet.add(PStringUtils.fieldName2Column(field));
        }
        consumer.accept(fieldSet, fieldColumnSet);
    }

    public static String fetchLogicDeleteCondition(String model) {
        PamirsTableInfo tableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
        if (tableInfo == null) {
            return null;
        }
        if (tableInfo.getLogicDelete()) {
            String logicDeleteColumn = tableInfo.getLogicDeleteColumn();
            String columnFormat = tableInfo.getColumnFormat();
            if (StringUtils.isNotBlank(columnFormat)) {
                logicDeleteColumn = String.format(columnFormat, logicDeleteColumn);
            }
            return StringHelper.concat(CharacterConstants.SEPARATOR_BLANK,
                    logicDeleteColumn,
                    SqlConstants.EQ,
                    tableInfo.getLogicNotDeleteValue());
        }
        return null;
    }

    public static <T> ModelFieldConfig fetchModelFieldConfig(Getter<T, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        Class<?> clazz = LambdaUtil.fetchClazz(getter);
        return TableInfoFetcher.fetchModelFieldConfig(clazz, fieldName);
    }

    public static <T extends IdModel, V extends IdModel> void listFieldQueryByIds(List<T> list,
                                                                                  Class<V> clazz,
                                                                                  Supplier<V> supplier,
                                                                                  Getter<T, List<Long>> idsGetter,
                                                                                  BiConsumer<T, List<V>> setter) {
        Map<Long, Map<Long, List<V>>> map = new HashMap<>(list.size() * 2);
        for (T item : list) {
            List<Long> ids = idsGetter.apply(item);
            if (CollectionUtils.isEmpty(ids)) {
                continue;
            }
            List<V> targetList = null;
            Long itemId = item.getId();
            for (Long id : ids) {
                Map<Long, List<V>> targetMap = map.computeIfAbsent(id, k -> new HashMap<>(2));
                if (targetList == null) {
                    targetList = targetMap.get(itemId);
                    if (targetList == null) {
                        targetList = new ArrayList<>();
                        setter.accept(item, targetList);
                    }
                }
                targetMap.put(itemId, targetList);
            }
        }
        List<V> deploymentConfigures = FetchUtil.fetchListByIds(clazz, map.keySet());
        for (V item : deploymentConfigures) {
            MapHelper.getIfPresent(map, item.getId(), v -> {
                for (List<V> targetItem : v.values()) {
                    targetItem.add(CopyHelper.simpleReplace(item, supplier.get()));
                }
            });
        }
    }

    //endregion

    //region batch fetch

    /**
     * <h>批量查询关联属性</h>
     * <p>
     * 1、当需要对多个属性进行查询时，请使用{@link FetchUtil#listFieldQuery(ReadApi, List, ModelFieldConfig, int)}<br/>
     * 2、当需要控制查询功能时，请使用{@link FetchUtil#listFieldQuery(ReadApi, List, Getter)}<br/>
     * 默认使用{@link pro.shushi.pamirs.meta.base.manager.data.DataManager}
     * </p>
     *
     * @param needQueryList 需要查询的列表
     * @param fieldGetter   属性的getter方法
     * @param <T>           源类型
     * @param <R>           目标类型
     */
    public static <T, R> int listFieldQuery(List<T> needQueryList, Getter<T, R> fieldGetter) {
        return listFieldQuery(Models.origin(), needQueryList, fetchModelFieldConfig(fieldGetter), -1);
    }

    public static <T, R, DM extends ReadApi> int listFieldQuery(DM dataManager, List<T> needQueryList, Getter<T, R> fieldGetter) {
        return listFieldQuery(dataManager, needQueryList, fetchModelFieldConfig(fieldGetter), -1);
    }

    public static <T, DM extends ReadApi> int listFieldQuery(DM dataManager, List<T> needQueryList, ModelFieldConfig modelFieldConfig, int maxQuerySize) {
        String field = modelFieldConfig.getField();
        String references = modelFieldConfig.getReferences();
        List<String> relationFields = modelFieldConfig.getRelationFields();
        List<String> referenceFields = modelFieldConfig.getReferenceFields();
        if (CollectionUtils.isEmpty(relationFields) || CollectionUtils.isEmpty(referenceFields)) {
            return -1;
        }
        String ttype = modelFieldConfig.getTtype();
        if (TtypeEnum.RELATED.value().equals(ttype)) {
            ttype = modelFieldConfig.getRelatedTtype();
        }
        int total;
        if (TtypeEnum.isRelationOne(ttype)) {
            total = listFieldQueryByRelationOne(dataManager, needQueryList, field, references, relationFields, referenceFields, maxQuerySize);
        } else if (TtypeEnum.O2M.value().equals(ttype)) {
            total = listFieldQueryByO2M(dataManager, needQueryList, field, references, relationFields, referenceFields, maxQuerySize);
        } else if (TtypeEnum.M2M.value().equals(ttype)) {
            String through = modelFieldConfig.getThrough();
            List<String> throughRelationFields = modelFieldConfig.getThroughRelationFields();
            List<String> throughReferenceFields = modelFieldConfig.getThroughReferenceFields();
            if (StringUtils.isBlank(through)
                    || CollectionUtils.isEmpty(throughRelationFields) || CollectionUtils.isEmpty(throughReferenceFields)) {
                return -1;
            }
            total = listFieldQueryByM2M(dataManager, needQueryList, field, references, relationFields, referenceFields, through, throughRelationFields, throughReferenceFields, maxQuerySize);
        } else {
            return -1;
        }
        return total;
    }

    private static <T, DM extends ReadApi> int listFieldQueryByRelationOne(DM dataManager, List<T> needQueryList, String field,
                                                                           String references, List<String> relationFields, List<String> referenceFields, int maxQuerySize) {
        List<Object> resultList = queryReferenceList(dataManager, needQueryList, references, relationFields, referenceFields, maxQuerySize);
        if (resultList == null) {
            return -1;
        }
        matchData(needQueryList, resultList, field, relationFields, referenceFields, false);
        return resultList.size();
    }

    private static <T, DM extends ReadApi> int listFieldQueryByO2M(DM dataManager, List<T> needQueryList, String field,
                                                                   String references, List<String> relationFields, List<String> referenceFields, int maxQuerySize) {
        List<Object> resultList = queryReferenceList(dataManager, needQueryList, references, relationFields, referenceFields, maxQuerySize);
        if (resultList == null) {
            return -1;
        }
        matchData(needQueryList, resultList, field, relationFields, referenceFields, true);
        return resultList.size();
    }

    private static <T, DM extends ReadApi> int listFieldQueryByM2M(DM dataManager, List<T> needQueryList,
                                                                   String field, String references, List<String> relationFields, List<String> referenceFields,
                                                                   String through, List<String> throughRelationFields, List<String> throughReferenceFields, int maxQuerySize) {
        List<Object> throughResultList = queryReferenceList(dataManager, needQueryList, through, relationFields, throughRelationFields, maxQuerySize);
        if (throughResultList == null) {
            return -1;
        }
        List<Object> resultList = queryReferenceList(dataManager, throughResultList, references, throughReferenceFields, referenceFields, maxQuerySize);
        if (resultList == null) {
            return -1;
        }
        MemoryIterableSearchCache<String, Object> resultCache = new MemoryIterableSearchCache<>(resultList, object -> generatorKey(object, referenceFields));
        Map<String, List<Object>> cache = new HashMap<>(resultList.size());
        for (Object throughResultItem : throughResultList) {
            String relationKey = generatorKey(throughResultItem, throughRelationFields),
                    referenceKey = generatorKey(throughResultItem, throughReferenceFields);
            Object value = resultCache.get(referenceKey);
            if (value == null) {
                continue;
            }
            cache.computeIfAbsent(relationKey, k -> new ArrayList<>()).add(value);
        }
        for (Object origin : needQueryList) {
            List<Object> value = cache.get(generatorKey(origin, relationFields));
            if (value != null) {
                FieldUtils.setFieldValue(origin, field, value);
            }
        }
        return throughResultList.size();
    }

    @SuppressWarnings("unchecked")
    private static <T, V, DM extends ReadApi> List<V> queryReferenceList(DM dataManager, List<T> relationList, String references, List<String> relationFields, List<String> referenceFields, int maxQuerySize) {
        ModelConfig referenceModelConfig = PamirsSession.getContext().getModelConfig(references);
        MemoryIterableSearchCache<String, ModelFieldConfig> referenceModelFieldConfigCache = new MemoryIterableSearchCache<>(referenceModelConfig.getModelFieldConfigList(), ModelFieldConfig::getName);
        int relationFieldSize = relationFields.size();
        Map<String, List<Object>> referenceFieldValueMap;
        if (relationFieldSize == 1) {
            referenceFieldValueMap = quickFillReferenceFieldValueMap(relationList, relationFields.get(0), referenceFields.get(0));
        } else {
            referenceFieldValueMap = standardFillReferenceFieldValueMap(relationList, relationFields, referenceFields);
        }
        if (referenceFieldValueMap.isEmpty()) {
            return null;
        }
        List<String> columnList = new ArrayList<>(relationFieldSize);
        List<Object>[] valuesList = new ArrayList[relationFieldSize];
        int index = 0;
        int currentQuerySize = 0;
        for (Map.Entry<String, List<Object>> entry : referenceFieldValueMap.entrySet()) {
            columnList.add(ModelFieldConfigWrapper.wrap(referenceModelFieldConfigCache.get(entry.getKey())).getSqlSelect(true));
            List<Object> value = entry.getValue();
            valuesList[index++] = value;
            currentQuerySize = Math.max(currentQuerySize, value.size());
        }
        if (maxQuerySize != -1 && currentQuerySize > maxQuerySize) {
            return null;
        }
        int batchSize = BeanDefinitionUtils.getBean(PamirsMapperConfigurationProxy.class).batchOperationForModel(references).getRead();
        if (batchSize <= 0) {
            batchSize = 500;
        }
        List<V> resultList = new ArrayList<>();
        int from = 0;
        do {
            int to = from + batchSize;
            List<?>[] referenceValueList = new ArrayList[relationFieldSize];
            int realTo = Math.min(to, currentQuerySize);
            for (int i = 0; i < relationFieldSize; i++) {
                referenceValueList[i] = new ArrayList<>(ListUtils.sub(valuesList[i], from, realTo));
            }
            resultList.addAll(dataManager.queryListByWrapper(Pops.<V>query()
                    .from(references)
                    .in(columnList, referenceValueList)));
            from = to;
        } while (from < currentQuerySize);
        if (CollectionUtils.isEmpty(resultList)) {
            return null;
        }
        return resultList;
    }

    private static <T> Map<String, List<Object>> quickFillReferenceFieldValueMap(List<T> relationList, String relationField, String referenceField) {
        int listSize = relationList.size();
        Map<String, List<Object>> referenceFieldValueMap = new HashMap<>(1);
        List<Object> referenceFieldValueList = new ArrayList<>(listSize);
        referenceFieldValueMap.put(referenceField, referenceFieldValueList);
        Set<Object> repeatReferenceFieldValueSet = new HashSet<>(listSize);
        for (Object item : relationList) {
            boolean isAddValue = true;
            Object value;
            if (relationField.startsWith(CharacterConstants.SEPARATOR_OCTOTHORPE) && relationField.endsWith(CharacterConstants.SEPARATOR_OCTOTHORPE)) {
                value = relationField.substring(1, relationField.length() - 1);
            } else {
                value = FieldUtils.getFieldValue(item, relationField);
            }
            if (value == null) {
                isAddValue = false;
            } else {
                if (referenceField.startsWith(CharacterConstants.SEPARATOR_OCTOTHORPE) && referenceField.endsWith(CharacterConstants.SEPARATOR_OCTOTHORPE)) {
                    isAddValue = referenceField.substring(1, referenceField.length() - 1).equals(value);
                } else {
                    int addBeforeSize = repeatReferenceFieldValueSet.size();
                    repeatReferenceFieldValueSet.add(value);
                    if (addBeforeSize == repeatReferenceFieldValueSet.size()) {
                        isAddValue = false;
                    }
                }
            }
            if (isAddValue) {
                referenceFieldValueList.add(value);
            }
        }
        if (referenceFieldValueList.isEmpty()) {
            return Collections.emptyMap();
        }
        return referenceFieldValueMap;
    }

    private static <T> Map<String, List<Object>> standardFillReferenceFieldValueMap(List<T> relationList, List<String> relationFields, List<String> referenceFields) {
        int relationFieldSize = relationFields.size();
        Map<String, List<Object>> referenceFieldValueMap = new HashMap<>(relationFieldSize);
        Map<String, Set<Object>> repeatReferenceFieldValueMap = new HashMap<>(relationFieldSize);
        for (Object item : relationList) {
            Map<String, Object> referenceFieldValueItem = new HashMap<>(relationFieldSize);
            boolean isAddValue = true;
            for (int i = 0; i < relationFieldSize; i++) {
                String relationField = relationFields.get(i);
                Object value;
                if (relationField.startsWith(CharacterConstants.SEPARATOR_OCTOTHORPE) && relationField.endsWith(CharacterConstants.SEPARATOR_OCTOTHORPE)) {
                    value = relationField.substring(1, relationField.length() - 1);
                } else {
                    value = FieldUtils.getFieldValue(item, relationField);
                }
                if (value == null) {
                    isAddValue = false;
                } else {
                    String referenceField = referenceFields.get(i);
                    if (referenceField.startsWith(CharacterConstants.SEPARATOR_OCTOTHORPE) && referenceField.endsWith(CharacterConstants.SEPARATOR_OCTOTHORPE)) {
                        isAddValue = referenceField.substring(1, referenceField.length() - 1).equals(value);
                    } else {
                        referenceFieldValueItem.put(referenceField, value);
                    }
                }
            }
            if (isAddValue) {
                boolean isRepeat = true;
                Map<String, List<Object>> temp = new HashMap<>(relationFieldSize);
                for (Map.Entry<String, Object> entry : referenceFieldValueItem.entrySet()) {
                    String fieldKey = entry.getKey();
                    Object value = entry.getValue();
                    Set<Object> repeatReferenceFieldValueSet = repeatReferenceFieldValueMap.computeIfAbsent(fieldKey, k -> new HashSet<>());
                    int addBeforeSize = repeatReferenceFieldValueSet.size();
                    repeatReferenceFieldValueSet.add(value);
                    isRepeat = isRepeat && addBeforeSize == repeatReferenceFieldValueSet.size();
                    temp.computeIfAbsent(fieldKey, k -> new ArrayList<>()).add(entry.getValue());
                }
                if (!isRepeat) {
                    for (Map.Entry<String, List<Object>> tempItem : temp.entrySet()) {
                        referenceFieldValueMap.computeIfAbsent(tempItem.getKey(), v -> new ArrayList<>()).addAll(tempItem.getValue());
                    }
                }
            }
        }
        return referenceFieldValueMap;
    }

    private static <T, V> void matchData(List<T> originList, List<V> targetList, String field, List<String> relationFields, List<String> referenceFields, boolean isCollection) {
        if (isCollection) {
            Map<String, List<V>> cache = new HashMap<>();
            for (V target : targetList) {
                cache.computeIfAbsent(generatorKey(target, referenceFields), k -> new ArrayList<>()).add(target);
            }
            for (T origin : originList) {
                List<V> value = cache.get(generatorKey(origin, relationFields));
                if (value != null) {
                    FieldUtils.setFieldValue(origin, field, value);
                }
            }
        } else {
            MemoryIterableSearchCache<String, V> cache = new MemoryIterableSearchCache<>(targetList, target -> generatorKey(target, referenceFields));
            for (Object origin : originList) {
                Object value = cache.get(generatorKey(origin, relationFields));
                if (value != null) {
                    FieldUtils.setFieldValue(origin, field, value);
                }
            }
        }
    }

    private static String generatorKey(Object target, List<String> fields) {
        StringBuilder builder = new StringBuilder();
        for (String field : fields) {
            if (builder.length() != 0) {
                builder.append(CharacterConstants.SEPARATOR_OCTOTHORPE);
            }
            if (field.startsWith(CharacterConstants.SEPARATOR_OCTOTHORPE) && field.endsWith(CharacterConstants.SEPARATOR_OCTOTHORPE)) {
                builder.append(field, 1, field.length() - 1);
            } else {
                builder.append(StringHelper.valueOf(FieldUtils.getFieldValue(target, field)));
            }
        }
        return builder.toString();
    }

    //endregion

    //region take method

    public static <T extends IdModel> T createOrUpdate(T data) {
        Models.origin().createOrUpdate(data);
        return data;
    }

    public static <T extends IdModel> List<T> createOrUpdateBatch(List<T> data) {
        Models.origin().createOrUpdateBatch(data);
        return data;
    }

    public static <T extends IdModel> Result<T> onlyCreate(T data) {
        T oldData = fetchOne(data);
        if (oldData == null) {
            data = Models.origin().createOne(data);
            return new Result<T>().setData(data);
        } else {
            return new Result<T>().error().setData(oldData);
        }
    }

    public static <T extends IdModel> Result<T> onlyUpdate(T data) {
        T oldData = fetchOne(data);
        if (oldData == null) {
            return new Result<T>().error();
        } else {
            Integer result = data.updateById();
            if (result == 0) {
                return new Result<T>().error().setData(oldData);
            } else {
                return new Result<T>().setData(data);
            }
        }
    }

    public static <T extends IdModel> List<Result<T>> onlyCreateBatch(List<T> list) {
        return batchConsumer0(list, (list0, results) -> {
            for (T item : list0) {
                results.add(onlyCreate(item));
            }
        });
    }


    public static <T extends IdModel> List<Result<T>> onlyUpdateBatch(List<T> list) {
        return batchConsumer0(list, (list0, results) -> {
            for (T item : list0) {
                results.add(onlyUpdate(item));
            }
        });
    }

    private static <T extends IdModel> List<Result<T>> batchConsumer0(List<T> list, BiConsumer<List<T>, List<Result<T>>> consumer) {
        List<Result<T>> results = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            results.add(new Result<T>().error());
            return results;
        }
        consumer.accept(list, results);
        return results;
    }

    /**
     * 一对多属性保存
     *
     * @param data               数据
     * @param model              {@link V}模型编码
     * @param getter             getter方法
     * @param setter             setter方法
     * @param uniqueKeyGenerator 唯一键生成器
     * @param <T>                存储模型类型
     * @param <V>                存储模型类型
     * @param <K>                唯一键类型
     */
    public static <T extends IdModel, V extends IdModel, K> void fieldSave(T data,
                                                                           String model,
                                                                           Getter<T, List<V>> getter,
                                                                           BiConsumer<T, List<V>> setter,
                                                                           UniqueKeyGenerator<V, K> uniqueKeyGenerator) {
        fieldSave(data, model, getter, setter, uniqueKeyGenerator, null);
    }

    /**
     * 一对多属性保存
     *
     * @param data               数据
     * @param model              {@link V}模型编码
     * @param getter             getter方法
     * @param setter             setter方法
     * @param uniqueKeyGenerator 唯一键生成器
     * @param beforeConsumer     前置消费
     * @param <T>                存储模型
     * @param <V>                存储模型
     * @param <K>                唯一键类型
     */
    public static <T extends IdModel, V extends IdModel, K> void fieldSave(T data,
                                                                           String model,
                                                                           Getter<T, List<V>> getter,
                                                                           BiConsumer<T, List<V>> setter,
                                                                           UniqueKeyGenerator<V, K> uniqueKeyGenerator,
                                                                           Consumer<V> beforeConsumer) {
        List<V> targetList = getter.apply(data);
        List<V> originList = getter.apply(data.fieldQuery(getter));
        setter.accept(data, targetList);
        MemoryIterableSearchCache<K, V> cache = new MemoryIterableSearchCache<>(originList, uniqueKeyGenerator);
        List<V> createList = new ArrayList<>(),
                updateList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(targetList)) {
            for (V target : targetList) {
                if (beforeConsumer != null) {
                    beforeConsumer.accept(target);
                }
                V origin = cache.compute(uniqueKeyGenerator.generator(target), (k, v) -> v);
                if (origin == null) {
                    Long id = target.getId();
                    if (id == null) {
                        createList.add(target);
                    } else {
                        if (CollectionUtils.isNotEmpty(originList)) {
                            boolean isCreate = true;
                            for (V item : originList) {
                                if (id.equals(item.getId())) {
                                    item.enableReentry();
                                    updateList.add(item);
                                    isCreate = false;
                                    break;
                                }
                            }
                            if (isCreate) {
                                target.unsetId();
                                createList.add(target);
                            }
                        }
                    }
                } else {
                    updateList.add(target);
                }
            }
        }
        cache.fill();
        OriginDataManager dataManager = Models.origin();
        if (!createList.isEmpty()) {
            dataManager.createBatch(createList);
        }
        if (!updateList.isEmpty()) {
            dataManager.updateBatch(updateList);
        }
        Collection<V> deleteList = cache.getNotComputedCache().values();
        if (!deleteList.isEmpty()) {
            Set<Long> ids = new HashSet<>(deleteList.size());
            for (V item : deleteList) {
                if (Models.modelDirective().isReentry(item)) {
                    continue;
                }
                ids.add(item.getId());
            }
            if (!ids.isEmpty()) {
                dataManager.deleteByWrapper(Pops.<T>lambdaQuery()
                        .from(model)
                        .in(T::getId, ids));
            }
        }
    }

    /**
     * 多对多属性保存
     *
     * @param data                数据
     * @param getter              getter方法
     * @param setter              setter方法
     * @param uniqueKeyGenerator  唯一键生成器
     * @param newInstanceFunction 中间模型实例构造函数
     * @param <T>                 存储模型类型
     * @param <V>                 存储模型类型
     * @param <M>                 关联关系中间模型类型
     * @param <K>                 唯一键类型
     */
    public static <T extends IdModel, V extends IdModel, M extends BaseRelation, K> void fieldSave(T data,
                                                                                                   Getter<T, List<V>> getter,
                                                                                                   BiConsumer<T, List<V>> setter,
                                                                                                   UniqueKeyGenerator<V, K> uniqueKeyGenerator,
                                                                                                   BiFunction<T, V, M> newInstanceFunction) {
        fieldSave(data, getter, setter, uniqueKeyGenerator, newInstanceFunction, null);
    }

    /**
     * 多对多属性保存
     *
     * @param data                数据
     * @param getter              getter方法
     * @param setter              setter方法
     * @param uniqueKeyGenerator  唯一键生成器
     * @param newInstanceFunction 中间模型实例构造函数
     * @param beforeConsumer      前置消费
     * @param <T>                 存储模型类型
     * @param <V>                 存储模型类型
     * @param <M>                 关联关系中间模型类型
     * @param <K>                 唯一键类型
     */
    public static <T extends IdModel, V extends IdModel, M extends BaseRelation, K> void fieldSave(T data,
                                                                                                   Getter<T, List<V>> getter,
                                                                                                   BiConsumer<T, List<V>> setter,
                                                                                                   UniqueKeyGenerator<V, K> uniqueKeyGenerator,
                                                                                                   BiFunction<T, V, M> newInstanceFunction,
                                                                                                   Consumer<V> beforeConsumer) {
        List<V> targetList = getter.apply(data);
        List<V> originList = getter.apply(data.fieldQuery(getter));
        setter.accept(data, targetList);
        MemoryIterableSearchCache<K, V> cache = new MemoryIterableSearchCache<>(originList, uniqueKeyGenerator);
        List<M> createList = new ArrayList<>(),
                updateList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(targetList)) {
            for (V target : targetList) {
                if (beforeConsumer != null) {
                    beforeConsumer.accept(target);
                }
                V origin = cache.compute(uniqueKeyGenerator.generator(target), (k, v) -> v);
                if (origin == null) {
                    createList.add(newInstanceFunction.apply(data, target));
                } else {
                    updateList.add(newInstanceFunction.apply(data, target));
                }
            }
        }
        cache.fill();
        OriginDataManager dataManager = Models.origin();
        if (!createList.isEmpty()) {
            dataManager.createBatch(createList);
        }
        if (!updateList.isEmpty()) {
            dataManager.updateBatch(updateList);
        }
        Collection<V> deleteList = cache.getNotComputedCache().values();
        if (!deleteList.isEmpty()) {
            List<M> deleteTargetList = new ArrayList<>();
            for (V item : deleteList) {
                if (Models.modelDirective().isReentry(item)) {
                    continue;
                }
                deleteTargetList.add(newInstanceFunction.apply(data, item));
            }
            if (!deleteTargetList.isEmpty()) {
                dataManager.deleteByPks(deleteTargetList);
            }
        }
    }

    public static <T extends IdModel, V extends IdModel> void setDataByIds(T data, Getter<T, List<V>> valueGetter, BiConsumer<T, List<Long>> idsSetter) {
        setDataByIds(data, valueGetter, idsSetter, null);
    }

    public static <T extends IdModel, V extends IdModel> void setDataByIds(T data, Getter<T, List<V>> valueGetter, BiConsumer<T, List<Long>> idsSetter, Supplier<RuntimeException> idNullThrow) {
        List<V> values = valueGetter.apply(data);
        if (values == null) {
            idsSetter.accept(data, null);
            return;
        }
        List<Long> ids = new ArrayList<>(values.size());
        for (V value : values) {
            Long id = value.getId();
            if (id == null) {
                if (idNullThrow == null) {
                    continue;
                }
                RuntimeException runtimeException = idNullThrow.get();
                if (runtimeException == null) {
                    continue;
                } else {
                    throw runtimeException;
                }
            }
            ids.add(id);
        }
        idsSetter.accept(data, ids);
    }

    //endregion

    //region predict method

    public static <T extends IdModel> boolean isNeedQueryOne(T data) {
        if (data == null) {
            return false;
        }
        Map<String, Object> dMap = data.get_d();
        String model = Models.api().getModel(data);
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        AtomicBoolean predict = new AtomicBoolean(true);
        consumerPkAndUniqueSet0(modelConfig, (pkSet, uniqueSetList) -> predict.set(isNeedQueryOne(dMap, pkSet, uniqueSetList)));
        return predict.get();
    }

    private static <T extends IdModel> boolean isNeedQueryOne(Map<String, Object> dMap, Set<String> pkSet, List<Set<String>> uniqueSetList) {
        int baseFieldCount = 0;
        for (String baseField : BASE_FIELD_LIST) {
            Object value = dMap.get(baseField);
            if (value != null) {
                baseFieldCount++;
            }
        }
        int pkCount = 0;
        for (String pk : pkSet) {
            Object value = dMap.get(pk);
            if (value != null) {
                pkCount++;
            }
        }
        Set<String> uniqueKeySet = new HashSet<>(uniqueSetList.size());
        for (Set<String> uniqueSet : uniqueSetList) {
            uniqueKeySet.addAll(uniqueSet);
        }
        int uniqueCount = 0;
        for (String unique : uniqueKeySet) {
            Object value = dMap.get(unique);
            if (value != null) {
                uniqueCount++;
            }
        }
        boolean isFullPk = pkCount == pkSet.size(),
                isFullUnique = uniqueCount == uniqueKeySet.size(),
                hasOtherField = dMap.size() - baseFieldCount - pkCount - uniqueCount != 0;
        if (!isFullPk) {
            return isFullUnique && !hasOtherField;
        }
        return !isFullUnique || !hasOtherField;
    }

    public static <T extends IdModel> boolean isNeedDeleteOne(T data) {
        if (data == null) {
            return false;
        }
        Map<String, Object> dMap = data.get_d();
        return dMap.containsKey(VariableNameConstants.id) && dMap.get(VariableNameConstants.id) == null;
    }

    public static <T extends IdModel> boolean isContainAllKeys(T data, Set<String> keys) {
        if (data == null) {
            return false;
        }
        return MapHelper.isContainAllValues(data.get_d(), keys);
    }

    public static <T extends IdModel> boolean isContainAllValues(T data, Set<String> keys) {
        if (data == null) {
            return false;
        }
        return MapHelper.isContainAllValues(data.get_d(), keys);
    }

    private static <T extends IdModel> boolean isContainAllKeys0(T data, Set<String> keys, MapHelper.AllKeyPredicate<String, Object> broken) {
        if (data == null) {
            return false;
        }
        return MapHelper.allKeyPredicate(data.get_d(), keys, broken);
    }

    //endregion

    //region unset method

    public static <T extends D> void unsetFieldValueByTtype(String model, T data, TtypeEnum... ttypes) {
        if (data == null) {
            return;
        }
        unsetFieldValuesByTtype(model, Collections.singletonList(data), ttypes);
    }

    public static <T extends D> void unsetFieldValuesByTtype(String model, List<T> list, TtypeEnum... ttypes) {
        String content = StringHelper.join(CharacterConstants.SEPARATOR_COMMA, TtypeEnum::value, ttypes);
        if (StringUtils.isBlank(content)) {
            return;
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        List<ModelFieldConfig> modelFieldConfigs = modelConfig.getModelFieldConfigList();
        List<ModelFieldConfig> selectModelFieldConfigs = new ArrayList<>();
        boolean isFirst = true;
        for (T item : list) {
            Map<String, Object> dMap = item.get_d();
            if (isFirst) {
                for (ModelFieldConfig modelFieldConfig : modelFieldConfigs) {
                    if (content.contains(modelFieldConfig.getTtype())) {
                        dMap.remove(modelFieldConfig.getLname());
                        selectModelFieldConfigs.add(modelFieldConfig);
                    }
                }
                isFirst = false;
            }
            for (ModelFieldConfig selectModelFieldConfig : selectModelFieldConfigs) {
                dMap.remove(selectModelFieldConfig.getLname());
            }
        }
    }

    //endregion

    /**
     * 获取启动模块
     *
     * @return 启动模块编码集合
     */
    public static Set<String> fetchBootModules() {
        Set<String> bootModules = Spider.getDefaultExtension(ModulesApi.class).modules();
        if (bootModules.isEmpty()) {
            BootModulesApi bootModulesApi = Spider.getDefaultExtension(BootModulesApi.class);
            PamirsBootMainProcessApi pamirsBootMainProcessApi = CommonApiFactory.getApi(PamirsBootMainProcessApi.class);
            return pamirsBootMainProcessApi.fetchBootModules(pamirsBootMainProcessApi.fetchSetupModuleMap(), bootModulesApi.modules(), bootModulesApi.excludeModules());
        }
        return bootModules;
    }
}
