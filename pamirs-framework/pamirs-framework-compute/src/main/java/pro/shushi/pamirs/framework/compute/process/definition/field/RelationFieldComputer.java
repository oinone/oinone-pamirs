package pro.shushi.pamirs.framework.compute.process.definition.field;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.systems.relation.RelationProcessor;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FieldAttributeConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_MODEL_CONFIG_IS_NOT_EXISTS_ERROR;

/**
 * 关联关系字段计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class RelationFieldComputer implements FieldComputer<Meta, ModelDefinition> {

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, ModelField field, ModelDefinition data) {
        RelationProcessor relationProcessor = CommonApiFactory.getApi(RelationProcessor.class);
        Result<Void> result = new Result<>();
        // 计算关联关系字段
        if (field.getRelationStore()) {
            // 计算关联关系重写字段
            if (SystemSourceEnum.isInherited(field.getSystemSource())) {
                // 继承
                relationProcessor.computeOverrideField(meta, field, field.getField(),
                        (rootModel, rootField) -> {
                            relationProcessor.makeDefaultRelationReferenceFields(meta, rootModel, rootField);
                            if (TtypeEnum.M2M.value().equals(rootField.getTtype().value())) {
                                relationProcessor.makeDefaultThrough(rootField);
                            }
                        });
            } else {
                String overrideField = (String) field.getAttribute(FieldAttributeConstants.OVERRIDE_FIELD);
                if (!StringUtils.isBlank(overrideField)) {
                    // 重写
                    relationProcessor.computeOverrideField(meta, field, overrideField,
                            (rootModel, rootField) -> {
                                relationProcessor.makeDefaultRelationReferenceFields(meta, rootModel, rootField);
                                if (TtypeEnum.M2M.value().equals(field.getTtype().value())) {
                                    ModelDefinition rootReference = meta.getModel(rootField.getReferences());
                                    relationProcessor.makeDefaultThrough(rootField);
                                    relationProcessor.makeDefaultThroughRelationReferenceFields(rootModel, rootReference, rootField);
                                }
                            });
                } else {
                    // 生成关联关系字段的默认关系字段和关联字段
                    relationProcessor.makeDefaultRelationReferenceFields(meta, data, field);
                    if (TtypeEnum.M2M.value().equals(field.getTtype().value())) {
                        relationProcessor.makeDefaultThrough(field);
                    }
                }
            }

            if (TtypeEnum.M2O.value().equals(field.getTtype().value())
                    || TtypeEnum.O2O.value().equals(field.getTtype().value())) {
                // 为O2O和M2O生成缺省关系字段
                relationProcessor.makeRelationFields(context, meta, data, field);
            } else if (TtypeEnum.O2M.value().equals(field.getTtype().value())) {
                String referencesModel = field.getReferences();
                ModelDefinition references = meta.getModel(referencesModel);
                if (references == null) {
                    throw PamirsException.construct(BASE_MODEL_CONFIG_IS_NOT_EXISTS_ERROR).appendMsg("model：" + referencesModel).errThrow();
                }
                // 为O2M生成缺省关联字段
                relationProcessor.makeReferenceFields(context, meta, references, field);
            }
        } else {
            boolean resetThrough = false;
            if (StringUtils.isNotBlank(field.getThrough())) {
                ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(field.getThrough());
                if (modelConfig == null) {
                    resetThrough = true;
                } else {
                    if (ModelTypeEnum.TRANSIENT.equals(modelConfig.getType())
                            || ModelTypeEnum.ABSTRACT.equals(modelConfig.getType())
                            || StringUtils.isBlank(modelConfig.getTable())) {
                        resetThrough = true;
                    }
                }
            }

            if (resetThrough) {
                field.setThrough(null);
            }
        }
        return result;
    }

}
