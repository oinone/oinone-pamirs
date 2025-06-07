package pro.shushi.pamirs.framework.orm;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationManager;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationReadApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationWriteApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.*;

/**
 * 递归写API实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@pro.shushi.pamirs.meta.annotation.Fun(BaseModel.MODEL_MODEL)
@Component
public class DefaultWriteWithFieldApi extends AbstractReadWriteApi implements WriteWithFieldApi, FunctionConstants {

    @Resource
    private RelationReadApi relationReadApi;

    @Resource
    private RelationWriteApi relationWriteApi;

    @Resource
    private RelationManager relationManager;

    @Transactional(rollbackFor = Throwable.class)
    @Action.Advanced(name = create, type = FunctionTypeEnum.CREATE, managed = true, invisible = ExpConstants.idValueExist, check = true)
    @Action(displayName = "创建", label = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = create)
    @Function.fun(create)
    @Override
    public <T> T createWithField(T data) {
        if (null == data) {
            return null;
        }
        String model = getModel(data);
        Fun.run(model, createOne, data);
        fieldSave(model, data);
        return data;
    }

    @Function.Advanced(displayName = "批量创建记录和关联关系", managed = true)
    @Function.fun(createWithFieldBatch)
    @Function
    @Override
    public <T> List<T> createWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        String model = getModel(dataList);
        Fun.run(model, createBatch, dataList);
        fieldSave(model, dataList);
        return dataList;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Action.Advanced(name = update, type = FunctionTypeEnum.UPDATE, managed = true, invisible = ExpConstants.idValueNotExist, check = true)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    @Function(name = update)
    @Function.fun(update)
    @Override
    public <T> T updateWithField(T data) {
        if (null == data) {
            return null;
        }
        String model = getModel(data);
        Fun.run(model, updateByPk, data);
        fieldSave(model, data);
        return data;
    }

    @Function.Advanced(displayName = "更新记录和关联关系", managed = true)
    @Function.fun(updateWithFieldBatch)
    @Function(openLevel = {LOCAL, REMOTE, API})
    @Override
    public <T> List<T> updateWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        String model = getModel(dataList);
        Fun.run(model, updateBatch, dataList);
        fieldSave(model, dataList);
        return dataList;
    }

    @Function.Advanced(displayName = "创建或更新记录和关联关系", managed = true)
    @Function.fun(createOrUpdateWithField)
    @Function
    @Override
    public <T> T createOrUpdateWithField(T data) {
        if (null == data) {
            return null;
        }
        String model = getModel(data);
        Fun.run(model, createOrUpdate, data);
        fieldSave(model, data);
        return data;
    }

    @Function.Advanced(displayName = "批量创建或更新记录和关联关系", managed = true)
    @Function.fun(createOrUpdateWithFieldBatch)
    @Function
    @Override
    public <T> List<T> createOrUpdateWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        String model = getModel(dataList);
        Fun.run(model, createOrUpdateBatch, dataList);
        fieldSave(model, dataList);
        return dataList;
    }

    @Function.Advanced(displayName = "删除记录和关联关系", managed = true)
    @Function.fun(delete)
    @Function
    @Override
    public <T> T deleteWithField(T data) {
        if (null == data) {
            return null;
        }
        String model = getModel(data);
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(model);
        for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
            relationWriteApi.relationDelete(modelFieldConfig, data);
        }
        Models.data().deleteByPk(data);
        return data;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Action.Advanced(name = delete, type = FunctionTypeEnum.DELETE, managed = true, priority = 66)
    @Action(displayName = "删除", label = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function(name = delete)
    @Function.fun(deleteWithFieldBatch)
    @Override
    public <T> List<T> deleteWithFieldBatch(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        String model = getModel(dataList);
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(model);
        for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
            relationWriteApi.listRelationDelete(modelFieldConfig, dataList);
        }
        Models.data().deleteByPks(dataList);
        return dataList;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void fieldSave(String model, Object data) {
        if (Models.modelDirective().isReentry(data)) {
            return;
        }
        if (!PamirsSession.directive().isBuiltAction()) {
            return;
        }
        Models.modelDirective().enableReentry(data);
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(model);
        for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
            if (modelFieldConfig.getRelationStore() && TtypeEnum.isRelationType(modelFieldConfig.getTtype())) {
                String fieldName = modelFieldConfig.getLname();
                if (data instanceof List) {
                    List dataList = (List) data;
                    relationWriteApi.listFieldSave(modelFieldConfig, dataList);

                    List originDataList = new ArrayList();
                    List currentDataList = new ArrayList();
                    List<Object> fieldValues = new ArrayList<>();
                    for (Object item : dataList) {
                        if (!FieldUtils.containsFieldValue(item, fieldName)) {
                            continue;
                        }
                        Object itemFieldValues = FieldUtils.getFieldValue(item, fieldName);
                        if (itemFieldValues instanceof List) {
                            List itemFieldValueList = (List) itemFieldValues;
                            if (CollectionUtils.isNotEmpty(itemFieldValueList)) {
                                fieldValues.addAll(itemFieldValueList);
                            }
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

                    if (CollectionUtils.isNotEmpty(fieldValues)) {
                        fieldSave(modelFieldConfig.getReferences(), fieldValues);
                    }
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

                    if (null != fieldValue) {
                        if (fieldValue instanceof List && CollectionUtils.isEmpty((List) fieldValue)) {
                            continue;
                        }
                        fieldSave(modelFieldConfig.getReferences(), fieldValue);
                    }
                }
            }
        }
    }

}
