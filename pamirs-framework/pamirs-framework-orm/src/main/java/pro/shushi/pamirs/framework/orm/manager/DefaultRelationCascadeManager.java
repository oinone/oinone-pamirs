package pro.shushi.pamirs.framework.orm.manager;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationCascadeManager;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationManager;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.GenericModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.OnCascadeEnum;
import pro.shushi.pamirs.meta.enmu.RtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_CASCADE_DELETE_FORBIDDEN_ERROR;
import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_CASCADE_UPDATE_FORBIDDEN_ERROR;

/**
 * 关系级联管理器实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class DefaultRelationCascadeManager implements RelationCascadeManager {

    @Resource
    private RelationManager relationManager;

    @Override
    public void onUpdateCheck(Object data, ModelConfig modelConfig) {
        if (null == data || CollectionUtils.isEmpty(modelConfig.getModelFieldConfigList())) {
            return;
        }
        if (data instanceof Collection) {
            //noinspection unchecked
            for (Object item : (List<Object>) data) {
                onUpdateCheck(item, modelConfig);
            }
        }
        for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
            if (RtypeEnum.isRelationType(modelFieldConfig.getTtype())) {
                onUpdateCheck(data, modelFieldConfig);
            }
        }
    }

    @Override
    public void onUpdateCheck(Object data, ModelConfig modelConfig, List<String> checkFields) {
        if (null == data || CollectionUtils.isEmpty(modelConfig.getModelFieldConfigList())) {
            return;
        }
        if (data instanceof Collection) {
            //noinspection unchecked
            for (Object item : (List<Object>) data) {
                onUpdateCheck(item, modelConfig, checkFields);
            }
        }
        for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
            if (RtypeEnum.isRelationType(modelFieldConfig.getTtype())
                    && relationManager.isIntersectionOf(checkFields, modelFieldConfig.getRelationFields())
            ) {
                onUpdateCheck(data, modelFieldConfig);
            }
        }
    }

    @Override
    public void onUpdateCheck(Object data, ModelFieldConfig modelFieldConfig) {
        if (null == data) {
            return;
        }
        if (RtypeEnum.M2O.value().equals(modelFieldConfig.getTtype()) || RtypeEnum.M2M.value().equals(modelFieldConfig.getTtype())) {
            return;
        }
        ModelField modelField = modelFieldConfig.getModelField();
        OnCascadeEnum onCascadeEnum = modelField.getOnUpdate();
        if (null == onCascadeEnum || onCascadeEnum.equals(OnCascadeEnum.NO_ACTION)) {
            return;
        }
        if (!Models.compute().isPkValueValid(data)) {
            return;
        }
        GenericModel newData = Models.generic(modelFieldConfig.getModel(), data);
        GenericModel oldData = newData.queryByPk();
        if (null == oldData) {
            return;
        }
        oldData.fieldQuery(modelFieldConfig.getLname());
        Object relationValue = FieldUtils.getFieldValue(oldData, modelFieldConfig.getLname());
        if (null == relationValue) {
            return;
        }
        if (!Models.compute().isPkValueValid(relationValue)) {
            return;
        }
        if (relationManager.isRelationFieldChange(modelFieldConfig, newData, oldData)) {
            switch (onCascadeEnum) {
                case SET_NULL: {
                    relationManager.setNullForRelation(modelFieldConfig.getReferences(), modelFieldConfig.getReferenceFields(), relationValue);
                }
                case CASCADE: {
                    relationManager.fillRelationFieldValuesFromOther(modelFieldConfig, newData, oldData);
                    relationManager.fillReferenceFieldValueFromRelation(modelFieldConfig, oldData);
                }
                case RESTRICT: {
                    Object oldFieldValue = FieldUtils.getFieldValue(oldData, modelFieldConfig.getLname());
                    if (null != oldFieldValue) {
                        //noinspection unchecked
                        if (TypeUtils.isCollection(oldFieldValue.getClass()) && CollectionUtils.isEmpty((List<Object>) oldFieldValue)) {
                            break;
                        }
                        throw PamirsException.construct(BASE_CASCADE_UPDATE_FORBIDDEN_ERROR)
                                .appendMsg("field:" + modelFieldConfig.getField()).errThrow();
                    }
                    break;
                }
                default: {
                    ModelConfig referenceModelConfig =
                            Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(modelFieldConfig.getReferences());
                    onUpdateCheck(relationValue, referenceModelConfig, modelFieldConfig.getReferenceFields());
                    if (relationValue instanceof Collection) {
                        Models.api().setDataModel(modelFieldConfig.getReferences(), relationValue);
                        //noinspection unchecked
                        Models.data().updateBatch((List<Object>) relationValue);
                    } else {
                        Models.generic(modelFieldConfig.getReferences(), relationValue).updateByPk();
                    }
                }
            }
        }
    }

    @Override
    public void onDeleteCheck(Object data, ModelConfig modelConfig) {
        if (null == data || CollectionUtils.isEmpty(modelConfig.getModelFieldConfigList())) {
            return;
        }
        if (data instanceof Collection) {
            //noinspection unchecked
            for (Object item : (List<Object>) data) {
                onDeleteCheck(item, modelConfig);
            }
        }
        for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
            if (RtypeEnum.isRelationType(modelFieldConfig.getTtype())) {
                onDeleteCheck(data, modelFieldConfig);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onDeleteCheck(Object data, ModelFieldConfig modelFieldConfig) {
        if (null == data) {
            return;
        }
        if (RtypeEnum.M2O.value().equals(modelFieldConfig.getTtype()) || RtypeEnum.M2M.value().equals(modelFieldConfig.getTtype())) {
            return;
        }
        ModelField modelField = modelFieldConfig.getModelField();
        OnCascadeEnum onCascadeEnum = modelField.getOnDelete();
        if (null == onCascadeEnum || onCascadeEnum.equals(OnCascadeEnum.NO_ACTION)) {
            return;
        }
        GenericModel genericData = Models.generic(modelFieldConfig.getModel(), data);
        genericData.fieldQuery(modelFieldConfig.getLname());
        Object relationValue = FieldUtils.getFieldValue(genericData, modelFieldConfig.getLname());
        if (null == relationValue) {
            return;
        }
        if (!Models.compute().isPkValueValid(relationValue)) {
            return;
        }
        switch (onCascadeEnum) {
            case SET_NULL: {
                relationManager.setNullForRelation(modelFieldConfig.getReferences(), modelFieldConfig.getReferenceFields(), relationValue);
                ModelConfig referenceModelConfig =
                        Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(modelFieldConfig.getReferences());
                onUpdateCheck(relationValue, referenceModelConfig, modelFieldConfig.getReferenceFields());
                if (relationValue instanceof Collection) {
                    Models.api().setDataModel(modelFieldConfig.getReferences(), relationValue);
                    //noinspection unchecked
                    Models.data().updateBatch((List) relationValue);
                } else {
                    Models.generic(modelFieldConfig.getReferences(), relationValue).updateByPk();
                }
                break;
            }
            case CASCADE: {
                ModelConfig referenceModelConfig =
                        Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(modelFieldConfig.getReferences());
                onDeleteCheck(relationValue, referenceModelConfig);
                if (relationValue instanceof Collection) {
                    Models.api().setDataModel(modelFieldConfig.getReferences(), relationValue);
                    //noinspection unchecked
                    Models.data().deleteByPks((List) relationValue);
                } else {
                    Models.generic(modelFieldConfig.getReferences(), relationValue).deleteByPk();
                }
                break;
            }
            case RESTRICT: {
                if (TypeUtils.isCollection(relationValue.getClass()) && CollectionUtils.isEmpty((List) relationValue)) {
                    break;
                }
                throw PamirsException.construct(BASE_CASCADE_DELETE_FORBIDDEN_ERROR)
                        .appendMsg("field:" + modelFieldConfig.getField()).errThrow();
            }
            default:
                break;
        }
    }

}
