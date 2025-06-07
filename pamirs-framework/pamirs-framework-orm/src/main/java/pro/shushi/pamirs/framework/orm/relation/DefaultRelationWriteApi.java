package pro.shushi.pamirs.framework.orm.relation;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationWriteApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.manager.data.FieldDataManager;
import pro.shushi.pamirs.meta.enmu.RtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 关联关系处理器默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@Slf4j
@Component
public class DefaultRelationWriteApi extends AbstractRelationWriteApi implements RelationWriteApi {

    @Override
    public <T> void fieldSave(ModelFieldConfig relation, T data) {
        if (!relation.getRelationStore()) {
            return;
        }
        Object fieldValueObj = FieldUtils.getFieldValue(data, relation.getLname());
        // 处理字段值与关系
        if (null != fieldValueObj) {
            // 字段值处理
            if (RtypeEnum.isRelationMany(relation.getTtype())) {
                List<Object> insertFieldValueList = new ArrayList<>();
                List<Object> updateFieldValueList = new ArrayList<>();
                List<Object> insertOrUpdateFieldValueList = new ArrayList<>();
                // 处理many字段值与one2many关系
                dealToManySaveRelation(relation, data, insertFieldValueList, updateFieldValueList, insertOrUpdateFieldValueList);
                // 存储字段值
                FieldDataManager.getInstance().createWithFieldBatch(insertFieldValueList);
                FieldDataManager.getInstance().updateWithFieldBatch(updateFieldValueList);
                FieldDataManager.getInstance().createOrUpdateWithFieldBatch(insertOrUpdateFieldValueList);
                // many2many关系处理
                if (RtypeEnum.M2M.value().equals(relation.getTtype())) {
                    //noinspection unchecked
                    List<Object> throughList = relationManager.fetchThroughListFromRelation(relation, data, (List<Object>) fieldValueObj);
                    Models.data().createOrUpdateBatch(throughList);
                }
            } else if (RtypeEnum.isRelationOne(relation.getTtype())) {
                // one字段值处理
                relationStrategyApi.submit(relation.getReferences(), relation.getReferenceFields(), fieldValueObj,
                        FieldDataManager.getInstance()::createWithField, FieldDataManager.getInstance()::updateWithField, FieldDataManager.getInstance()::createOrUpdateWithField
                );
                if (relationManager.isToOneRelationChange(relation, data)) {
                    // one关系处理
                    relationManager.fillRelationFieldValueFromRelation(relation, data);
                    Models.origin().submit(data, Models.data()::createOne, Models.data()::updateByUniqueField, Models.data()::createOrUpdate);
                }
            }
        }
    }

    @Override
    public <T> void fieldSave(String model, String relation, T data) {
        ModelFieldConfig modelFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(model, relation);
        fieldSave(modelFieldConfig, data);
    }

    @Override
    public <T> void listFieldSave(ModelFieldConfig relation, List<T> dataList) {
        if (!relation.getRelationStore()) {
            return;
        }
        List<Object> saveDataList = null;
        List<Object> updateDataList = null;
        List<Object> saveOrUpdateDataList = null;
        List<Object> insertFieldValueList = new ArrayList<>();
        List<Object> updateFieldValueList = new ArrayList<>();
        List<Object> insertOrUpdateFieldValueList = new ArrayList<>();
        List<Object> saveThroughList = null;
        // 处理字段值与关系
        for (T data : dataList) {
            Object fieldValueObj = FieldUtils.getFieldValue(data, relation.getLname());
            if (null != fieldValueObj) {
                if (RtypeEnum.isRelationMany(relation.getTtype())) {
                    // 处理many字段值与one2many关系
                    dealToManySaveRelation(relation, data, insertFieldValueList, updateFieldValueList, insertOrUpdateFieldValueList);
                    // many2many关系处理
                    if (RtypeEnum.M2M.value().equals(relation.getTtype())) {
                        if (null == saveThroughList) {
                            saveThroughList = new ArrayList<>();
                        }
                        //noinspection unchecked
                        List<Object> throughList = relationManager.fetchThroughListFromRelation(relation, data, (List<Object>) fieldValueObj);
                        if (CollectionUtils.isNotEmpty(throughList)) {
                            saveThroughList.addAll(throughList);
                        }
                    }
                } else if (RtypeEnum.isRelationOne(relation.getTtype())) {
                    // one字段值处理
                    relationStrategyApi.submit(relation.getReferences(), relation.getReferenceFields(), fieldValueObj,
                            insertFieldValueList::add, updateFieldValueList::add, insertOrUpdateFieldValueList::add
                    );
                }
            }
        }
        FieldDataManager.getInstance().createWithFieldBatch(insertFieldValueList);
        FieldDataManager.getInstance().updateWithFieldBatch(updateFieldValueList);
        FieldDataManager.getInstance().createOrUpdateWithFieldBatch(insertOrUpdateFieldValueList);
        Models.data().createOrUpdateBatch(saveThroughList);
        // one关系回填处理
        for (T data : dataList) {
            Object fieldValueObj = FieldUtils.getFieldValue(data, relation.getLname());
            if (null != fieldValueObj) {
                if (RtypeEnum.isRelationOne(relation.getTtype()) && relationManager.isToOneRelationChange(relation, data)) {
                    if (null == saveDataList) {
                        saveDataList = new ArrayList<>();
                    }
                    if (null == updateDataList) {
                        updateDataList = new ArrayList<>();
                    }
                    if (null == saveOrUpdateDataList) {
                        saveOrUpdateDataList = new ArrayList<>();
                    }
                    relationManager.fillRelationFieldValueFromRelation(relation, data);
                    Models.origin().submit(data, saveDataList::add, updateDataList::add, saveOrUpdateDataList::add);
                }
            }
        }
        Models.data().createBatch(saveDataList);
        Models.data().updateBatch(updateDataList);
        Models.data().createOrUpdateBatch(saveOrUpdateDataList);
    }

    @Override
    public <T> void listFieldSave(String model, String relation, List<T> dataList) {
        ModelFieldConfig modelFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(model, relation);
        listFieldSave(modelFieldConfig, dataList);
    }

    @Override
    public <T> void relationDelete(ModelFieldConfig relation, T data) {
        if (!relation.getRelationStore()) {
            return;
        }
        Object fieldValueObj = FieldUtils.getFieldValue(data, relation.getLname());
        if (null != fieldValueObj) {
            if (RtypeEnum.isRelationMany(relation.getTtype())) {
                List<Object> updateFieldValueList = new ArrayList<>();
                List<Object> deleteThroughList = new ArrayList<>();
                dealToManyDeleteRelation(relation, data, fieldValueObj, updateFieldValueList, deleteThroughList);
                Models.data().updateBatch(updateFieldValueList);
                Models.data().deleteByUniques(deleteThroughList);
            }
//            } else if (RtypeEnum.isRelationOne(relation.getTtype())) {
//                relationManager.setNullForRelation(relation.getModel(), relation.getRelationFields(), data);
//                Models.data().createOrUpdate(data);
//            }
            FieldUtils.setFieldValue(data, relation.getLname(), null);
        }
    }

    @Override
    public <T> void relationDelete(String model, String relation, T data) {
        ModelFieldConfig modelFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(model, relation);
        relationDelete(modelFieldConfig, data);
    }

    @Override
    public <T> void listRelationDelete(ModelFieldConfig relation, List<T> dataList) {
        if (!relation.getRelationStore()) {
            return;
        }
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        List<Object> updateValueList = null;
        List<Object> updateFieldValueList = null;
        List<Object> deleteThroughList = null;
        for (Object data : dataList) {
            Object fieldValueObj = FieldUtils.getFieldValue(data, relation.getLname());
            if (null != fieldValueObj) {
                if (RtypeEnum.isRelationMany(relation.getTtype())) {
                    if (null == updateFieldValueList) {
                        updateFieldValueList = new ArrayList<>();
                    }
                    if (null == deleteThroughList) {
                        deleteThroughList = new ArrayList<>();
                    }
                    dealToManyDeleteRelation(relation, data, fieldValueObj, updateFieldValueList, deleteThroughList);
                } else if (RtypeEnum.isRelationOne(relation.getTtype())) {
                    if (null == updateValueList) {
                        updateValueList = new ArrayList<>();
                    }
                    dealToOneDeleteRelation(relation, fieldValueObj, updateValueList);
                }
                FieldUtils.setFieldValue(data, relation.getLname(), null);
            }
        }
        Models.data().updateBatch(updateValueList);
        Models.data().updateBatch(updateFieldValueList);
        Models.data().deleteByUniques(deleteThroughList);
    }

    @Override
    public <T> void listRelationDelete(String model, String relation, List<T> dataList) {
        ModelFieldConfig modelFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(model, relation);
        listRelationDelete(modelFieldConfig, dataList);
    }

}
