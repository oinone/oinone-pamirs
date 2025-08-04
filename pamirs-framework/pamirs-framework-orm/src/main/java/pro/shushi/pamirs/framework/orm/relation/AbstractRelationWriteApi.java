package pro.shushi.pamirs.framework.orm.relation;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationManager;
import pro.shushi.pamirs.meta.api.core.orm.systems.strategy.RelationStrategyApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.OnCascadeEnum;
import pro.shushi.pamirs.meta.enmu.RtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_CASCADE_DELETE_FORBIDDEN_ERROR;
import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_SAVE_CURRENT_FIRST_ERROR;

/**
 * 关联关系处理器抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
public abstract class AbstractRelationWriteApi {

    @Resource
    protected RelationManager relationManager;

    @Resource
    protected RelationStrategyApi relationStrategyApi;

    @SuppressWarnings("unused")
    public <T> void dealToManySaveRelation(String model, String relation, T data,
                                           List<Object> insertFieldValueList, List<Object> updateFieldValueList,
                                           List<Object> insertOrUpdateFieldValueList) {
        ModelFieldConfig modelFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(model, relation);
        dealToManySaveRelation(modelFieldConfig, data, insertFieldValueList, updateFieldValueList, insertOrUpdateFieldValueList);
    }

    public <T> void dealToManySaveRelation(ModelFieldConfig relation, T data,
                                           List<Object> insertFieldValueList, List<Object> updateFieldValueList,
                                           List<Object> insertOrUpdateFieldValueList) {
        ModelFieldConfig modelFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(relation.getModel(), relation.getField());
        Object fieldValueObj = FieldUtils.getFieldValue(data, modelFieldConfig.getLname());
        if (!relationManager.isRelationFieldValid(modelFieldConfig, data)) {
            throw PamirsException.construct(BASE_SAVE_CURRENT_FIRST_ERROR).errThrow();
        }

        // one2many关系处理
        if (RtypeEnum.O2M.value().equals(modelFieldConfig.getTtype())) {
            relationManager.fillReferenceFieldValueFromRelation(modelFieldConfig, data);
        }
        // 字段值处理
        relationStrategyApi.submit(modelFieldConfig.getReferences(), modelFieldConfig.getReferenceFields(), fieldValueObj,
                insertFieldValueList::add, updateFieldValueList::add, insertOrUpdateFieldValueList::add
        );
    }

    public <T> void dealToManyDeleteRelation(ModelFieldConfig modelFieldConfig, T data, Object fieldValueObj,
                                             List<Object> updateFieldValueList, List<Object> deleteThroughList) {
        // one2many关系处理
        if (RtypeEnum.O2M.value().equals(modelFieldConfig.getTtype())) {
            ModelField modelField = modelFieldConfig.getModelField();
            OnCascadeEnum onCascade = modelField.getOnDelete();

            if (null == onCascade || OnCascadeEnum.SET_NULL.equals(onCascade)) {
                relationManager.setNullForRelation(modelFieldConfig.getReferences(), modelFieldConfig.getReferenceFields(), fieldValueObj);
                updateFieldValueList.addAll((List<?>) fieldValueObj);
            } else if (OnCascadeEnum.CASCADE.equals(onCascade)) {
                Models.api().setDataModel(modelFieldConfig.getReferences(), fieldValueObj);
                Models.data().deleteByPks((List<?>) fieldValueObj);
            } else if (OnCascadeEnum.RESTRICT.equals(onCascade)) {
                if (TypeUtils.isCollection(fieldValueObj.getClass()) && CollectionUtils.isEmpty((List<?>) fieldValueObj)) {
                    return;
                }
                throw PamirsException.construct(BASE_CASCADE_DELETE_FORBIDDEN_ERROR)
                        .appendMsg("field:" + modelFieldConfig.getField()).errThrow();
            }
            // else do nothing ...

        } else {
            //noinspection unchecked
            List<Object> throughList = relationManager.fetchThroughListFromRelation(modelFieldConfig, data, (List<Object>) fieldValueObj);
            if (CollectionUtils.isNotEmpty(throughList)) {
                deleteThroughList.addAll(throughList);
            }
        }
    }

    public void dealToOneDeleteRelation(ModelFieldConfig modelFieldConfig, Object fieldValueObj, List<Object> updateFieldValueList) {

        ModelField modelField = modelFieldConfig.getModelField();
        if (!RtypeEnum.O2O.value().equals(modelFieldConfig.getTtype())) {
            return;
        }
        OnCascadeEnum onCascade = modelField.getOnDelete();

        if (null == onCascade || OnCascadeEnum.SET_NULL.equals(onCascade)) {
            relationManager.setNullForRelation(modelFieldConfig.getReferences(), modelFieldConfig.getReferenceFields(), fieldValueObj);
            updateFieldValueList.add(fieldValueObj);
        } else if (OnCascadeEnum.CASCADE.equals(onCascade)) {
            Models.api().setDataModel(modelFieldConfig.getReferences(), fieldValueObj);
            Models.data().deleteByEntity(fieldValueObj);
        } else if (OnCascadeEnum.RESTRICT.equals(onCascade)) {
            throw PamirsException.construct(BASE_CASCADE_DELETE_FORBIDDEN_ERROR)
                    .appendMsg("field:" + modelFieldConfig.getField()).errThrow();
        }
        // else do nothing ...
    }
}
