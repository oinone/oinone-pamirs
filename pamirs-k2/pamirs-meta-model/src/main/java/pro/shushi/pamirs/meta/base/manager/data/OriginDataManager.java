package pro.shushi.pamirs.meta.base.manager.data;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.ReadApi;
import pro.shushi.pamirs.meta.api.core.orm.WriteApi;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.api.core.orm.clone.ReferenceUtils;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationManager;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationReadApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationWriteApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.strategy.DmStrategyApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.strategy.StrategyApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.MetaExpEnumerate;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_LIST_IS_EMPTY_ERROR;
import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_LIST_IS_UN_SUPPORT_ERROR;

/**
 * 源数据管理器
 * <p>
 * 2020/5/7 11:58 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Lazy
@Component
public class OriginDataManager implements ReadApi, WriteApi, WriteWithFieldApi, DmStrategyApi, FunctionConstants {

    @Resource
    private ReadApi defaultReadApi;

    @Resource
    private WriteApi defaultWriteApi;

    @Resource
    private WriteWithFieldApi defaultWriteWithFieldApi;

    @Resource
    private RelationReadApi defaultRelationReadApi;

    @Resource
    private RelationWriteApi defaultRelationWriteApi;

    @Resource
    private StrategyApi defaultStrategyApi;

    protected String getModel(Object... modelObject) {
        for (Object obj : modelObject) {
            String model = Models.api().getDataModel(obj);
            if (StringUtils.isNotBlank(model)) {
                return model;
            }
        }
        return null;
    }

    public static OriginDataManager getInstance() {
        return Models.origin();
    }

    @Override
    public <T> T createOne(T data) {
        excludeList(data);
        T result = defaultWriteApi.createOne(data);
        ReferenceUtils.deal(result, data);
        return data;
    }

    @Override
    public <T> Integer createOrUpdate(T data) {
        Result<T> result = createOrUpdateWithResult(data);
        return result.getEffectRows();
    }

    @Override
    public <T> Result<T> createOrUpdateWithResult(T data) {
        excludeList(data);
        Result<T> result = defaultWriteApi.createOrUpdateWithResult(data);
        ReferenceUtils.deal(result.getData(), data);
        return result;
    }

    @Override
    public <T> Integer updateByPk(T data) {
        excludeList(data);
        return defaultWriteApi.updateByPk(data);
    }

    @Override
    public <T> Integer updateByUniqueField(T data) {
        excludeList(data);
        return defaultWriteApi.updateByUniqueField(data);
    }

    @Override
    public <T> Integer updateByEntity(T data, T query) {
        excludeList(data);
        excludeList(query);
        return defaultWriteApi.updateByEntity(data, query);
    }

    @Override
    public <T> Integer updateByWrapper(T data, IWrapper<T> updateWrapper) {
        excludeList(data);
        return defaultWriteApi.updateByWrapper(data, updateWrapper);
    }

    @Override
    public <T> List<T> createBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result = defaultWriteApi.createBatch(dataList);
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    @Override
    public <T> List<T> createBatchWithSize(List<T> dataList, Integer batchSize) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result = defaultWriteApi.createBatchWithSize(dataList, batchSize);
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    @Override
    public <T> Integer createOrUpdateBatch(List<T> dataList) {
        Result<List<T>> result = createOrUpdateBatchWithResult(dataList);
        return result.getEffectRows();
    }

    @Override
    public <T> Result<List<T>> createOrUpdateBatchWithResult(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new Result<>();
        }
        Result<List<T>> result = defaultWriteApi.createOrUpdateBatchWithResult(dataList);
        ReferenceUtils.dealList(result.getData(), dataList);
        return result;
    }

    @Override
    public <T> Integer createOrUpdateBatchWithSize(List<T> dataList, Integer batchSize) {
        Result<List<T>> result = createOrUpdateBatchWithSizeWithResult(dataList, batchSize);
        return result.getEffectRows();
    }

    @Override
    public <T> Result<List<T>> createOrUpdateBatchWithSizeWithResult(List<T> dataList, Integer batchSize) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new Result<>();
        }
        Result<List<T>> result = defaultWriteApi.createOrUpdateBatchWithSizeWithResult(dataList, batchSize);
        ReferenceUtils.dealList(result.getData(), dataList);
        return result;
    }

    @Override
    public <T> Integer updateBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return 0;
        }
        return defaultWriteApi.updateBatch(dataList);
    }

    @Override
    public <T> Integer updateBatchWithSize(List<T> dataList, Integer batchSize) {
        if (CollectionUtils.isEmpty(dataList)) {
            return 0;
        }
        return defaultWriteApi.updateBatchWithSize(dataList, batchSize);
    }

    @Override
    public <T> Boolean deleteByPk(T data) {
        excludeList(data);
        return defaultWriteApi.deleteByPk(data);
    }

    @Override
    public <T> Boolean deleteByPks(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return true;
        }
        return defaultWriteApi.deleteByPks(dataList);
    }

    @Override
    public <T> Boolean deleteByUniqueField(T data) {
        excludeList(data);
        return defaultWriteApi.deleteByUniqueField(data);
    }

    @Override
    public <T> Boolean deleteByUniques(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return true;
        }
        return defaultWriteApi.deleteByUniques(dataList);
    }

    @Override
    public <T> Integer deleteByEntity(T query) {
        excludeList(query);
        return defaultWriteApi.deleteByEntity(query);
    }

    @Override
    public <T> Integer deleteByWrapper(IWrapper<T> queryWrapper) {
        return defaultWriteApi.deleteByWrapper(queryWrapper);
    }

    @Override
    public <T> void submit(T entity, Consumer<Object> insertConsumer, Consumer<Object> updateConsumer, Consumer<Object> insertOrUpdateConsumer) {
        defaultStrategyApi.submit(entity, insertConsumer, updateConsumer, insertOrUpdateConsumer);
    }

    @Override
    public <T> T queryByPk(T query) {
        excludeList(query);
        return defaultReadApi.queryByPk(query);
    }

    @Override
    public <T> T queryOne(T query) {
        excludeList(query);
        return defaultReadApi.queryOne(query);
    }

    @Override
    public <T> T queryOneByWrapper(IWrapper<T> queryWrapper) {
        return defaultReadApi.queryOneByWrapper(queryWrapper);
    }

    @Override
    public <T> List<T> queryListByEntity(T query) {
        excludeList(query);
        return defaultReadApi.queryListByEntity(query);
    }

    @Override
    public <T> List<T> queryListByEntityWithBatchSize(T query, Integer batchSize) {
        excludeList(query);
        return defaultReadApi.queryListByEntityWithBatchSize(query, batchSize);
    }

    @Override
    public <T> List<T> queryListByWrapper(IWrapper<T> queryWrapper) {
        return defaultReadApi.queryListByWrapper(queryWrapper);
    }

    @Override
    public <T> List<T> queryListByEntity(Pagination<T> page, T query) {
        excludeList(query);
        return defaultReadApi.queryListByEntity(page, query);
    }

    @Override
    public <T> List<T> queryListByWrapper(Pagination<T> page, IWrapper<T> queryWrapper) {
        return defaultReadApi.queryListByWrapper(page, queryWrapper);
    }

    @Override
    public <T> Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper) {
        return defaultReadApi.queryPage(page, queryWrapper);
    }

    @Override
    public <T> Long count(T query) {
        excludeList(query);
        return defaultReadApi.count(query);
    }

    @Override
    public <T> Long count(IWrapper<T> queryWrapper) {
        return defaultReadApi.count(queryWrapper);
    }

    public <T, D extends AbstractModel> T fieldQuery(T data, Getter<D, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return fieldQuery(data, fieldName);
    }

    public <T> T fieldQuery(T data, String fieldName) {
        excludeList(data);
        String model = getModel(data);
        ModelFieldConfig modelFieldConfig = getModelFieldConfig(model, fieldName);
        String ttype = modelFieldConfig.getTtype();
        if (TtypeEnum.isRelationType(ttype)) {
            Object fieldValue = Models.directive().run(() -> defaultRelationReadApi.queryFieldByRelation(modelFieldConfig, data));
            FieldUtils.setFieldValue(data, modelFieldConfig.getLname(), fieldValue);
        }
        return data;
    }

    public <T, D extends AbstractModel> List<T> listFieldQuery(List<T> dataList, Getter<D, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return listFieldQuery(dataList, fieldName);
    }

    public <T> List<T> listFieldQuery(List<T> dataList, String fieldName) {
        excludeEmptyList(dataList);
        String model = getModel(dataList);
        ModelFieldConfig modelFieldConfig = getModelFieldConfig(model, fieldName);
        return Models.directive().run(() -> defaultRelationReadApi.listFieldQueryByRelation(modelFieldConfig, dataList));
    }

    public <T, D extends AbstractModel> T fieldSave(T data, Getter<D, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return fieldSave(data, fieldName);
    }

    public <T> T fieldSave(T data, String fieldName) {
        excludeList(data);
        String model = getModel(data);
        String field = getModelField(model, fieldName);
        Models.modelDirective().clearAfterAllWithoutResult(() ->
                Models.directive().runWithoutResult(() -> defaultRelationWriteApi.fieldSave(model, field, data)), data);
        return data;
    }

    public <T, D extends AbstractModel> List<T> listFieldSave(List<T> dataList, Getter<D, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return listFieldSave(dataList, fieldName);
    }

    public <T> List<T> listFieldSave(List<T> dataList, String fieldName) {
        excludeEmptyList(dataList);
        String model = getModel(dataList);
        String field = getModelField(model, fieldName);
        Models.modelDirective().clearAfterAllWithoutResult(() ->
                Models.directive().runWithoutResult(() -> defaultRelationWriteApi.listFieldSave(model, field, dataList)), dataList);
        return dataList;
    }

    public <T extends AbstractModel> T fieldSaveOnCascade(T data, Getter<T, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return fieldSaveOnCascade(data, fieldName);
    }

    public <T extends AbstractModel> T fieldSaveOnCascade(T data, String fieldName) {
        excludeList(data);
        String model = getModel(data);
        ModelFieldConfig modelFieldConfig = getModelFieldConfig(model, fieldName);
        Models.modelDirective().clearAfterAllWithoutResult(() ->
                Models.directive().runWithoutResult(() -> fieldSaveOnCascade0(data, modelFieldConfig)), data);
        return data;
    }

    public <T extends AbstractModel> List<T> listFieldSaveOnCascade(List<T> data, Getter<T, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return listFieldSaveOnCascade(data, fieldName);
    }

    public <T extends AbstractModel> List<T> listFieldSaveOnCascade(List<T> list, String fieldName) {
        excludeEmptyList(list);
        String model = getModel(list);
        ModelFieldConfig modelFieldConfig = getModelFieldConfig(model, fieldName);
        Models.modelDirective().clearAfterAllWithoutResult(() ->
                Models.directive().runWithoutResult(() ->
                        fieldSaveOnCascade0(list, modelFieldConfig)), list);
        return list;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void fieldSaveOnCascade0(Object data, ModelFieldConfig modelFieldConfig) {
        String fieldName = modelFieldConfig.getLname();
        RelationReadApi relationReadApi = CommonApiFactory.getApi(RelationReadApi.class);
        RelationWriteApi relationWriteApi = CommonApiFactory.getApi(RelationWriteApi.class);
        RelationManager relationManager = CommonApiFactory.getApi(RelationManager.class);
        if (data instanceof List) {
            List dataList = (List) data;
            relationWriteApi.listFieldSave(modelFieldConfig, dataList);

            List originDataList = new ArrayList<>();
            List currentDataList = new ArrayList<>();
            for (Object item : dataList) {
                if (!FieldUtils.containsFieldValue(item, fieldName)) {
                    continue;
                }
                Object itemFieldValues = FieldUtils.getFieldValue(item, fieldName);
                if (itemFieldValues instanceof List) {
                    Object originData = TypeUtils.getNewInstance(item.getClass());
                    relationManager.fillRelationFieldValuesFromOther(modelFieldConfig, item, originData);
                    originDataList.add(originData);
                    currentDataList.add(item);
                }
            }

            originDataList = relationReadApi.listFieldQueryByRelation(modelFieldConfig, originDataList);

            ModelConfig referencesModelConfig = PamirsSession.getContext().getModelConfig(modelFieldConfig.getReferences());

            int i = 0;
            for (Object originData : originDataList) {
                List originFieldValues = (List) FieldUtils.getFieldValue(originData, fieldName);
                List currentFieldValues = (List) FieldUtils.getFieldValue(currentDataList.get(i), fieldName);
                List deleteList = relationManager.fetchDeleteRelationList(referencesModelConfig, originFieldValues, currentFieldValues);
                FieldUtils.setFieldValue(originData, fieldName, deleteList);
                ++i;
            }
            relationWriteApi.listRelationDelete(modelFieldConfig, originDataList);
        } else {
            relationWriteApi.fieldSave(modelFieldConfig, data);
            Object fieldValue = FieldUtils.getFieldValue(data, fieldName);

            if (FieldUtils.containsFieldValue(data, fieldName) && TtypeEnum.isRelationMany(modelFieldConfig.getTtype())) {
                Object originData = TypeUtils.getNewInstance(data.getClass());
                relationManager.fillRelationFieldValuesFromOther(modelFieldConfig, data, originData);

                List currentFieldValues = (List) fieldValue;
                List originFieldValues = (List) relationReadApi.queryFieldByRelation(modelFieldConfig, data);

                ModelConfig referencesModelConfig = PamirsSession.getContext().getSimpleModelConfig(modelFieldConfig.getReferences());

                List deleteList = relationManager.fetchDeleteRelationList(referencesModelConfig, originFieldValues, currentFieldValues);
                FieldUtils.setFieldValue(originData, fieldName, deleteList);
                relationWriteApi.relationDelete(modelFieldConfig, originData);
            }
        }
    }

    public <T, D extends AbstractModel> T relationDelete(T data, Getter<D, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return relationDelete(data, fieldName);
    }

    public <T> T relationDelete(T data, String fieldName) {
        excludeList(data);
        String model = getModel(data);
        String field = getModelField(model, fieldName);
        Models.directive().runWithoutResult(() -> defaultRelationWriteApi.relationDelete(model, field, data));
        return data;
    }

    public <T, D extends AbstractModel> List<T> listRelationDelete(List<T> dataList, Getter<D, ?> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return listRelationDelete(dataList, fieldName);
    }

    public <T> List<T> listRelationDelete(List<T> dataList, String fieldName) {
        excludeEmptyList(dataList);
        String model = getModel(dataList);
        String field = getModelField(model, fieldName);
        Models.directive().runWithoutResult(() -> defaultRelationWriteApi.listRelationDelete(model, field, dataList));
        return dataList;
    }

    @Override
    public <T> T createWithField(T data) {
        excludeList(data);
        T result = Models.modelDirective().clearAfterAll(() ->
                Models.directive().run(() -> Fun.run(getModel(data), create, data),
                        SystemDirectiveEnum.FROM_CLIENT), data);
        ReferenceUtils.deal(result, data);
        return data;
    }

    @Override
    public <T> List<T> createWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result = Models.modelDirective().clearAfterAll(() ->
                Models.directive().run(() -> defaultWriteWithFieldApi.createWithFieldBatch(dataList),
                        SystemDirectiveEnum.FROM_CLIENT), dataList);
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    @Override
    public <T> T updateWithField(T data) {
        excludeList(data);
        T result = Models.modelDirective().clearAfterAll(() ->
                Models.directive().run(() -> defaultWriteWithFieldApi.updateWithField(data),
                        SystemDirectiveEnum.FROM_CLIENT), data);
        ReferenceUtils.deal(result, data);
        return data;
    }

    @Override
    public <T> List<T> updateWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result = Models.modelDirective().clearAfterAll(() ->
                Models.directive().run(() -> defaultWriteWithFieldApi.updateWithFieldBatch(dataList),
                        SystemDirectiveEnum.FROM_CLIENT), dataList);
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    @Override
    public <T> T createOrUpdateWithField(T data) {
        excludeList(data);
        T result = Models.modelDirective().clearAfterAll(() ->
                Models.directive().run(() -> defaultWriteWithFieldApi.createOrUpdateWithField(data),
                        SystemDirectiveEnum.FROM_CLIENT), data);
        ReferenceUtils.deal(result, data);
        return data;
    }

    @Override
    public <T> List<T> createOrUpdateWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        List<T> result = Models.modelDirective().clearAfterAll(() ->
                Models.directive().run(() -> defaultWriteWithFieldApi.createOrUpdateWithFieldBatch(dataList),
                        SystemDirectiveEnum.FROM_CLIENT), dataList);
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

    @Override
    public <T> T deleteWithField(T data) {
        excludeList(data);
        return Models.directive().run(() -> defaultWriteWithFieldApi.deleteWithField(data), SystemDirectiveEnum.FROM_CLIENT);
    }

    @Override
    public <T> List<T> deleteWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        return Models.directive().run(() -> defaultWriteWithFieldApi.deleteWithFieldBatch(dataList), SystemDirectiveEnum.FROM_CLIENT);
    }

    public void excludeEmptyList(List<?> list) {
        if (list == null || list.isEmpty()) {
            throw PamirsException.construct(BASE_LIST_IS_EMPTY_ERROR).errThrow();
        }
    }

    public void excludeList(Object obj) {
        if (obj instanceof Collection) {
            throw PamirsException.construct(BASE_LIST_IS_UN_SUPPORT_ERROR).errThrow();
        }
    }

    private <T> String getModel(T data) {
        String model = Models.api().getModel(data);
        if (model == null) {
            throw PamirsException.construct(MetaExpEnumerate.BASE_FETCH_MODEL_ERROR).errThrow();
        }
        return model;
    }

    private ModelFieldConfig getModelFieldConfig(String model, String fieldName) {
        ModelFieldConfig modelField = PamirsSession.getContext().getModelFieldByFieldName(model, fieldName);
        if (modelField == null) {
            throw PamirsException.construct(MetaExpEnumerate.BASE_FETCH_MODEL_FIELD_ERROR).errThrow();
        }
        return modelField;
    }

    private String getModelField(String model, String fieldName) {
        ModelFieldConfig modelField = PamirsSession.getContext().getModelFieldByFieldName(model, fieldName);
        if (modelField == null) {
            throw PamirsException.construct(MetaExpEnumerate.BASE_FETCH_MODEL_FIELD_ERROR).errThrow();
        }
        return modelField.getField();
    }
}
