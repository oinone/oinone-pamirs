package pro.shushi.pamirs.framework.orm.manager;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationManager;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.base.GenericModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.*;

import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.*;

/**
 * 关系管理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Slf4j
@Component
public class DefaultRelationManager implements RelationManager {

    @Override
    public boolean isRelationFieldChange(ModelFieldConfig fieldConfig, Object newData, Object oldData) {
        List<Object> newRelationFieldValues = fetchRelationFieldValues(fieldConfig, newData);
        List<Object> oldRelationFieldValues = fetchRelationFieldValues(fieldConfig, oldData);
        if (newRelationFieldValues.size() != oldRelationFieldValues.size()) {
            throw PamirsException.construct(BASE_RELATION_FIELD_NUM_ERROR)
                    .appendMsg("field:" + fieldConfig.getField()).errThrow();
        }
        int i = 0;
        for (Object value : newRelationFieldValues) {
            Object oldValue = oldRelationFieldValues.get(i);
            if (null == value) {
                return null != oldValue;
            } else {
                if (!value.equals(oldValue)) {
                    return true;
                }
            }
            i++;
        }
        return false;
    }

    @Override
    public boolean isToOneRelationChange(ModelFieldConfig fieldConfig, Object data) {
        List<Object> relationFieldValues = fetchRelationFieldValues(fieldConfig, data);
        List<Object> referenceFieldValues = fetchToOneReferenceFieldValues(fieldConfig, data);
        if (null == relationFieldValues && null == referenceFieldValues) {
            return false;
        }
        if (null == relationFieldValues || null == referenceFieldValues) {
            return true;
        }
        if (relationFieldValues.size() != referenceFieldValues.size()) {
            throw PamirsException.construct(BASE_RELATION_FIELD_NUM2_ERROR)
                    .appendMsg("field:" + fieldConfig.getField()).errThrow();
        }
        int i = 0;
        for (Object relationValue : relationFieldValues) {
            Object referenceValue = referenceFieldValues.get(i);
            if (null == relationValue) {
                return null != referenceValue;
            } else {
                if (!relationValue.equals(referenceValue)) {
                    return true;
                }
            }
            i++;
        }
        return false;
    }

    @Override
    public boolean isRelationFieldValid(ModelFieldConfig fieldConfig, Object data) {
        List<String> relationFields = fieldConfig.getRelationFields();
        for (String relationField : relationFields) {
            ModelFieldConfig relationFieldConfig = PamirsSession.getContext().getModelField(fieldConfig.getModel(), relationField);
            if (FieldUtils.isConstantRelationFieldValue(relationField)) {
                continue;
            }
            if (null == FieldUtils.getFieldValue(data, relationFieldConfig.getLname())) {
                if (log.isDebugEnabled()) {
                    log.debug("relation field is null. model: {}, field: {}, relationField: {}", fieldConfig.getModel(), fieldConfig.getField(), relationFieldConfig.getField());
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isReferenceFieldValid(ModelFieldConfig fieldConfig, Object data) {
        List<String> referenceFields = fieldConfig.getReferenceFields();
        for (String referenceField : referenceFields) {
            ModelFieldConfig referenceFieldConfig = PamirsSession.getContext().getModelField(fieldConfig.getModel(), referenceField);
            if (null == FieldUtils.getFieldValue(data, referenceFieldConfig.getLname())) {
                if (log.isDebugEnabled()) {
                    log.debug("reference field is null. model: {}, field: {}, referenceField: {}", fieldConfig.getModel(), fieldConfig.getField(), referenceFieldConfig.getField());
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isIntersectionOf(List<String> fields1, List<String> fields2) {
        return fields1.retainAll(fields2);
    }

    @Override
    public List<Object> fetchRelationFieldValues(ModelFieldConfig fieldConfig, Object data) {
        List<String> relationFields = fieldConfig.getRelationFields();
        List<Object> relationFieldValues = new ArrayList<>();
        for (String relationField : relationFields) {
            relationFieldValues.add(FieldUtils.getRelationFieldValue(data, fieldConfig.getModel(), relationField));
        }
        return relationFieldValues;
    }

    @Override
    public List<Object> fetchToOneReferenceFieldValues(ModelFieldConfig fieldConfig, Object data) {
        Object fieldValue = FieldUtils.getFieldValue(data, fieldConfig.getLname());
        if (null == fieldValue) {
            return null;
        }
        List<String> referenceFields = fieldConfig.getReferenceFields();
        List<Object> referenceFieldValues = new ArrayList<>();
        for (String referenceField : referenceFields) {
            ModelFieldConfig referenceFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(fieldConfig.getReferences(), referenceField);
            referenceFieldValues.add(FieldUtils.getFieldValue(fieldValue, referenceFieldConfig.getLname()));
        }
        return referenceFieldValues;
    }

    @Override
    public List<Object> fetchThroughKeyFieldValues(Object value, String through, List<String> throughRelationFields, List<String> throughReferenceFields) {
        List<Object> keys = new ArrayList<>();
        for (String throughRelationField : throughRelationFields) {
            ModelFieldConfig throughFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(through, throughRelationField);
            keys.add(FieldUtils.getFieldValue(value, throughFieldConfig.getLname()));
        }
        for (String throughReferenceField : throughReferenceFields) {
            ModelFieldConfig throughFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(through, throughReferenceField);
            keys.add(FieldUtils.getFieldValue(value, throughFieldConfig.getLname()));
        }
        return keys;
    }

    @Override
    public List<Object> fetchDeleteRelationList(ModelConfig modelConfig, List<Object> existRelationList, List<Object> currentRelationList) {
        if (CollectionUtils.isEmpty(existRelationList)) {
            return existRelationList;
        }
        boolean empty = CollectionUtils.isEmpty(currentRelationList);
        List<Object> deleteRelationList = new ArrayList<>();
        Set<Object> completedSet = new HashSet<>();
        XXX:
        for (Object existItem : existRelationList) {
            if (!empty) {
                for (Object currentItem : currentRelationList) {
                    if (completedSet.contains(currentItem)) {
                        continue;
                    }
                    if (Models.compute().equalsByPks(modelConfig, existItem, currentItem)
                            || Models.compute().equalsByUniqueKey(modelConfig, existItem, currentItem)) {
                        completedSet.add(currentItem);
                        continue XXX;
                    }
                }
            }
            deleteRelationList.add(existItem);
        }
        return deleteRelationList;
    }

    @Override
    public List<Object> fetchThroughListFromRelation(ModelFieldConfig fieldConfig, Object data, List<Object> fieldValues) {
        if (CollectionUtils.isEmpty(fieldValues)) {
            return null;
        }
        String through = fieldConfig.getThrough();
        List<String> relationFields = fieldConfig.getRelationFields();
        List<String> throughRelationFields = fieldConfig.getThroughRelationFields();
        List<String> referenceFields = fieldConfig.getReferenceFields();
        List<String> throughReferenceFields = fieldConfig.getThroughReferenceFields();
        List<Object> newThroughList = new ArrayList<>();
        for (Object fieldValue : fieldValues) {
            GenericModel throughObj = Models.generic(through, new HashMap<>());
            fillSingleReferenceFieldValueFromRelation(data, throughObj, fieldConfig.getModel(), fieldConfig.getThrough(),
                    relationFields, throughRelationFields);
            fillSingleReferenceFieldValueFromRelation(fieldValue, throughObj, fieldConfig.getReferences(), fieldConfig.getThrough(),
                    referenceFields, throughReferenceFields);
            newThroughList.add(throughObj);
        }
        return newThroughList;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setNullForRelation(String model, List<String> setNullFields, Object data) {
        if (null == data) {
            return;
        }
        if (List.class.isAssignableFrom(data.getClass())) {
            for (Object v : (List) data) {
                setNullForRelation(model, setNullFields, v);
            }
        } else {
            if (!CollectionUtils.isEmpty(setNullFields)) {
                for (String setNullField : setNullFields) {
                    ModelFieldConfig setNullFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(model, setNullField);
                    FieldUtils.setFieldValue(data, setNullFieldConfig.getLname(), null);
                }
            }
        }
    }

    @Override
    public void fillRelationFieldValuesFromOther(ModelFieldConfig fieldConfig, Object originData, Object destData) {
        List<String> relationFields = fieldConfig.getRelationFields();
        for (String relationField : relationFields) {
            if (!FieldUtils.isConstantRelationFieldValue(relationField)) {
                ModelFieldConfig relationFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(fieldConfig.getModel(), relationField);
                if (null == relationFieldConfig) {
                    throw PamirsException.construct(BASE_RELATION_FIELD_CONFIG_ERROR)
                            .appendMsg("field:" + relationField).errThrow();
                }
                if (Models.compute().hasField(originData, relationFieldConfig.getLname())) {
                    Object oldFieldData = FieldUtils.getFieldValue(originData, relationFieldConfig.getLname());
                    FieldUtils.setFieldValue(destData, relationFieldConfig.getLname(), oldFieldData);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fillManyToOneValueFromRelation(ModelFieldConfig fieldConfig, Object data) {
        if (null == fieldConfig.getRelationStore() || !fieldConfig.getRelationStore()) {
            return;
        }
        Map<String, Object> dMap = Models.d(data);
        if (TtypeEnum.isRelationOne(fieldConfig.getTtype()) && !CollectionUtils.isEmpty(fieldConfig.getRelationFields())) {
            if (fieldConfig.getRelationFields().size() != fieldConfig.getReferenceFields().size()) {
                throw PamirsException.construct(BASE_RELATION_FIELD_OR_REFERENCE_FIELD_NUM_ERROR)
                        .appendMsg("model:" + fieldConfig.getModel() + ",field:" + fieldConfig.getField()).errThrow();
            }
            Object m2o;
            if (null == dMap.get(fieldConfig.getLname())) {
                if (TypeUtils.isModelClass(fieldConfig.getLtype())) {
                    m2o = TypeUtils.getNewInstance(fieldConfig.getLtype());
                } else {
                    m2o = new HashMap<>();
                }
            } else {
                m2o = dMap.get(fieldConfig.getLname());
            }
            int i = 0;
            for (String relationField : fieldConfig.getRelationFields()) {
                if (!FieldUtils.isConstantRelationFieldValue(relationField)) {
                    ModelFieldConfig relationFieldConfig = PamirsSession.getContext().getModelField(fieldConfig.getModel(), relationField);
                    if (null == relationFieldConfig) {
                        throw PamirsException.construct(BASE_RELATION_FIELD_IS_NOT_EXISTS_ERROR)
                                .appendMsg("model:" + fieldConfig.getModel() + ",field:" + fieldConfig.getField() + ",relation field:" + relationField)
                                .errThrow();
                    }
                    String referenceField = fieldConfig.getReferenceFields().get(i);
                    ModelFieldConfig referenceFieldConfig = PamirsSession.getContext().getModelField(fieldConfig.getReferences(), referenceField);
                    if (null == referenceFieldConfig) {
                        throw PamirsException.construct(BASE_RELATION_MODEL_IS_NOT_EXISTS_ERROR)
                                .appendMsg("model:" + fieldConfig.getModel() + ",field:" + fieldConfig.getField()
                                        + ",field.reference:" + fieldConfig.getReferences() + ",field.reference.field:" + referenceField + ", 请检查该模型【" + fieldConfig.getReferences() + "】的模块是否安装")
                                .errThrow();
                    }
                    Object fieldValue = dMap.get(relationFieldConfig.getColumn());
                    if (null == fieldValue) {
                        fieldValue = dMap.get(relationFieldConfig.getLname());
                    }
                    if (null == fieldValue) {
                        return;
                    }
                    if (m2o instanceof Map) {
                        ((Map<String, Object>) m2o).put(referenceFieldConfig.getLname(), fieldValue);
                    } else if (D.class.isAssignableFrom(m2o.getClass())) {
                        ((D) m2o).get_d().put(referenceFieldConfig.getLname(), fieldValue);
                    } else {
                        FieldUtils.setFieldValue(m2o, referenceFieldConfig.getLname(), fieldValue);
                    }
                }
                i++;
            }
            dMap.put(fieldConfig.getLname(), m2o);
        }
    }

    @Override
    public void fillRelationFieldValueFromRelation(ModelFieldConfig fieldConfig, Object data) {
        Map<String, Object> dMap = Models.d(data);
        if (TtypeEnum.isRelationOne(fieldConfig.getTtype()) && fieldConfig.getRelationStore() && !CollectionUtils.isEmpty(fieldConfig.getRelationFields())) {
            Object currentRelated = FieldUtils.getRelationFieldValue(dMap, fieldConfig.getModel(), fieldConfig.getField());
            if (null != currentRelated) {
                int index = 0;
                for (String relationField : fieldConfig.getRelationFields()) {
                    String referenceField = fieldConfig.getReferenceFields().get(index);
                    boolean fieldContainsKey = Models.d(currentRelated).containsKey(referenceField);
                    Object value = FieldUtils.getReferenceFieldValue(currentRelated, fieldConfig.getReferences(), referenceField);
                    if (!FieldUtils.isConstantRelationFieldValue(relationField)) {
                        ModelFieldConfig modelFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(fieldConfig.getModel(), relationField);
                        if (modelFieldConfig != null) {
                            String name = modelFieldConfig.getLname();
                            if (!dMap.containsKey(name) && fieldContainsKey) {
                                dMap.put(name, value);
                            }
                        }
                    }
                    index++;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fillReferenceFieldValueFromRelation(ModelFieldConfig fieldConfig, Object data) {
        if (TtypeEnum.M2M.value().equals(fieldConfig.getTtype())) {
            return;
        }
        Object referenceModelValue = FieldUtils.getFieldValue(data, fieldConfig.getLname());
        if (null == referenceModelValue) {
            return;
        }
        String references = fieldConfig.getReferences();
        List<String> relationFields = fieldConfig.getRelationFields();
        List<String> referenceFields = fieldConfig.getReferenceFields();
        if (List.class.isAssignableFrom(referenceModelValue.getClass())) {
            List<Object> fieldValues = (List<Object>) referenceModelValue;
            if (CollectionUtils.isEmpty(fieldValues)) {
                return;
            }
            for (Object fieldValue : fieldValues) {
                fillSingleReferenceFieldValueFromRelation(data, fieldValue, fieldConfig.getModel(), references, relationFields, referenceFields);
            }
        } else {
            fillSingleReferenceFieldValueFromRelation(data, referenceModelValue, fieldConfig.getModel(), references, relationFields, referenceFields);
        }
    }

    private void fillSingleReferenceFieldValueFromRelation(Object value, Object singleFieldValue,
                                                           String model, String references,
                                                           List<String> relationFields, List<String> referenceFields) {
        int i = 0;
        for (String relationField : relationFields) {
            String referenceField = referenceFields.get(i);
            ModelFieldConfig referenceFieldConfig = PamirsSession.getContext().getModelField(references, referenceField);
            if (FieldUtils.containsFieldValue(singleFieldValue, referenceFieldConfig.getLname())
                    && null == FieldUtils.getFieldValue(singleFieldValue, referenceFieldConfig.getLname())) {
                continue;
            }
            if (!FieldUtils.isConstantRelationFieldValue(relationField)) {
                ModelFieldConfig relationFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(model, relationField);
                if (null == relationFieldConfig) {
                    throw PamirsException.construct(BASE_RELATION_FIELD_CONFIG_ERROR)
                            .appendMsg("field:" + relationField).errThrow();
                }
                Object relationFieldValue = FieldUtils.getFieldValue(value, relationFieldConfig.getLname());
                FieldUtils.setFieldValue(singleFieldValue, referenceFieldConfig.getLname(), relationFieldValue);
            } else {
                FieldUtils.setFieldValue(singleFieldValue, referenceFieldConfig.getLname(), relationField.substring(1, relationField.length() - 1));
            }
            i++;
        }
    }

}
