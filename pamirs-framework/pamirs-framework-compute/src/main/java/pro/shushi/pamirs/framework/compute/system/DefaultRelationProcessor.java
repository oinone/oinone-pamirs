package pro.shushi.pamirs.framework.compute.system;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.framework.compute.InheritedComputeTemplate;
import pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.ModelDefinitionComputer;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.systems.relation.RelationProcessor;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.IdRelation;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.FieldAttributeConstants;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.ModelUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.*;

/**
 * 关联关系处理器默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@Slf4j
@Component
public class DefaultRelationProcessor implements RelationProcessor {

    @Override
    public String defaultReferenceFromField(Field field) {
        try {
            if (field.getType().isPrimitive()) {
                throw PamirsException.construct(BASE_FIELD_NOT_MODEL_ERROR).appendMsg("不是模型类型:" + field.getName()).errThrow();
            }
            if (TypeUtils.isCollection(field.getType())) {
                return TypeUtils.getActualType(field).getTypeName();
            } else {
                return field.getType().getName();
            }
        } catch (Exception e) {
            throw PamirsException.construct(BASE_CONVERT_RELATION_ERROR, e).errThrow();
        }
    }

    @Override
    public ModelField makeManyToOneField(ModelDefinition model, ModelDefinition references, String newField,
                                         List<String> relationFields, List<String> referenceFields) {
        ModelField newRelationField = new ModelField();
        newRelationField.setStore(Boolean.FALSE)
                .setDisplayName(model.getDisplayName())
                .setLname(newField)
                .setPriority(MetaDefaultConstants.PRIORITY_VALUE)
                .setModel(model.getModel())
                .setModelName(model.getName())
                .setRelationStore(Boolean.TRUE)
                .setName(newField)
                .setField(newField)
                .setTtype(TtypeEnum.M2O)
                .setRelationFields(relationFields)
                .setReferences(references.getModel())
                .setReferenceFields(referenceFields)
                .setSystemSource(SystemSourceEnum.RELATION)
        ;
        return newRelationField;
    }

    @Override
    public List<ModelField> makeManyToManyField(ComputeContext context, Meta meta,
                                                ModelDefinition relationModel, ModelDefinition throughModel, ModelDefinition referenceModel,
                                                ModelField relation, boolean newModel) {
        if (ModelTypeEnum.PROXY.equals(relationModel.getType())) {
            relationModel = meta.getModel(relationModel.getProxy());
        }
        if (null == throughModel.getModelFields()) {
            throughModel.setModelFields(new ArrayList<>());
        }
        // 补充关联关系字段默认的中间模型关系字段和关联字段配置
        makeDefaultThroughRelationReferenceFields(relationModel, referenceModel, relation);
        List<String> models = ModelUtils.sortNames(relationModel.getModel(), referenceModel.getModel());

        Map<String, ModelField> throughModelFieldMap = throughModel.getModelFields().stream().collect(Collectors.toMap(ModelField::getField, v -> v));
        List<String> relationFields = relation.getRelationFields();
        List<String> throughRelationFields = relation.getThroughRelationFields();
        List<String> throughReferenceFields = relation.getThroughReferenceFields();
        List<String> referenceFields = relation.getReferenceFields();

        List<ModelField> modelFieldList = new ArrayList<>();
        // 构造关系字段
        makeManyToManyFieldUnit(context, meta, models, modelFieldList, relation, newModel, throughModelFieldMap, throughModel,
                relationModel, relationFields, throughRelationFields, throughReferenceFields);
        // 构造关联字段
        makeManyToManyFieldUnit(context, meta, models, modelFieldList, relation, newModel, throughModelFieldMap, throughModel,
                referenceModel, referenceFields, throughReferenceFields, throughRelationFields);
        boolean isPkId = null != throughModel.getSuperModels() && throughModel.getSuperModels().contains(IdRelation.MODEL_MODEL);
        for (ModelField modelField : throughModel.getModelFields()) {
            if (isPkId) {
                if (!FieldConstants.ID.equals(modelField.getField())) {
                    if (null != modelField.getPk() && modelField.getPk()) {
                        log.warn(MessageFormat.format("{0},errorCode:{1}，model:{2},field:{3}",
                                BASE_THROUGH_MODEL_PK_ERROR.msg(), BASE_THROUGH_MODEL_PK_ERROR.code(), modelField.getModel(), modelField.getField()));
                    }
                    modelField.setPk(false);
                    modelField.setPkIndex(null);
                }
            }
            /**
             * 实际业务场景存在该场景,如果显示设置了不是关系字段为PrimaryKey，那么就应该为PrimaryKey
             else {
             if (!throughRelationFields.contains(modelField.getField()) && !throughReferenceFields.contains(modelField.getField())) {
             if (null != modelField.getPk() && modelField.getPk()) {
             log.warn(MessageFormat.format("{0},errorCode:{1}，model:{2},field:{3}",
             BASE_THROUGH_MODEL_PK2_ERROR.msg(), BASE_THROUGH_MODEL_PK2_ERROR.code(), modelField.getModel(), modelField.getField()));
             }
             modelField.setPk(false);
             modelField.setPkIndex(null);
             }
             }**/
        }
        return modelFieldList;
    }

    @Override
    public void makeDefaultThrough(ModelField relation) {
        if (StringUtils.isBlank(relation.getThrough()) && (null == relation.getRelationStore() || relation.getRelationStore())) {
            List<String> models = ModelUtils.sortNames(relation.getModel(), relation.getReferences());
            ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(models.get(0));
            String module = modelConfig.getModule();
            List<String> names = ModelUtils.sortAndSplitNames(relation.getModel(), relation.getReferences());
            String through = module + CharacterConstants.SEPARATOR_DOT
                    + names.get(0) + FieldConstants.REL_SEPARATOR + PStringUtils.capitalize(names.get(1));
            relation.setThrough(through);
        }
    }

    @Override
    public void makeDefaultThroughRelationReferenceFields(ModelDefinition relationModel, ModelDefinition referenceModel, ModelField relation) {
        List<String> throughRelationFields = relation.getThroughRelationFields();
        if (CollectionUtils.isEmpty(throughRelationFields)) {
            throughRelationFields = makeThroughRelationReferenceFields(relationModel, relation, throughRelationFields,
                    BASE_RELATION_NO_THROUGH_RELATION_FIELDS_NO_PK_ERROR);
            relation.setThroughRelationFields(throughRelationFields);
        }
        List<String> throughReferenceFields = relation.getThroughReferenceFields();
        if (CollectionUtils.isEmpty(throughReferenceFields)) {
            throughReferenceFields = makeThroughRelationReferenceFields(referenceModel, relation, throughReferenceFields,
                    BASE_RELATION_NO_THROUGH_REFERENCE_FIELDS_NO_PK_ERROR);
            relation.setThroughReferenceFields(throughReferenceFields);
        }
    }

    private List<String> makeThroughRelationReferenceFields(ModelDefinition entityModel, ModelField relation,
                                                            List<String> throughRelationReferenceFields,
                                                            ComputeExpEnumerate baseRelationNoThroughRelationFieldsNoPkError) {
        if (null == throughRelationReferenceFields) {
            throughRelationReferenceFields = new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(entityModel.getPk())
                && !ModelTypeEnum.TRANSIENT.value().equals(entityModel.getType().value())) {
            throw PamirsException.construct(baseRelationNoThroughRelationFieldsNoPkError)
                    .appendMsg(MessageFormat.format("model:{0},field:{1}", entityModel.getModel(), relation.getField())).errThrow();
        }
        for (String pk : entityModel.getPk()) {
            String relationFieldName = entityModel.getName() + StringUtils.capitalize(pk);
            throughRelationReferenceFields.add(relationFieldName);
        }
        return throughRelationReferenceFields;
    }

    private void makeManyToManyFieldUnit(ComputeContext context, Meta meta,
                                         List<String> models, List<ModelField> modelFieldList,
                                         ModelField relation, boolean newModel, Map<String, ModelField> throughModelFieldMap,
                                         ModelDefinition throughModel, ModelDefinition entityModel, List<String> entityFields,
                                         List<String> throughRelationFields, List<String> throughReferenceFields) {
        int baseIndex = entityModel.getModel().equals(models.get(0)) ? 0 : throughReferenceFields.size();
        int i = 0;
        boolean isPkId = null != throughModel.getSuperModels() && throughModel.getSuperModels().contains(IdRelation.MODEL_MODEL);
        for (String field : throughRelationFields) {
            ModelFieldConfig fieldConfig = null;
            if (FieldUtils.isConstantRelationFieldValue(entityFields.get(i))) {
                if (throughModelFieldMap.containsKey(field)) {
                    fieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(throughModel.getModel(), field);

                }
                if (fieldConfig == null) {
                    fieldConfig = Objects.requireNonNull(PamirsSession.getContext())
                            .getModelField(entityModel.getModel(), field);
                }
            } else {
                fieldConfig = Objects.requireNonNull(PamirsSession.getContext())
                        .getModelField(entityModel.getModel(), entityFields.get(i));

            }
            if (fieldConfig == null) {
                throw PamirsException.construct(BASE_M2M_RELATION_THROUGH_MODEL_ERROR)
                        .appendMsg(MessageFormat.format("模型:{0},字段:{1},relationFields:{2}", entityModel.getModel(), relation.getField(), entityFields.get(i))).errThrow();
            }

            TtypeEnum ttypeEnum = TtypeEnum.getEnumByValue(TtypeEnum.class, fieldConfig.getTtype());
            ModelField throughModelField = new ModelField()
                    .setTtype(ttypeEnum)
                    .setLtype(fieldConfig.getLtype())
                    .setLtypeT(fieldConfig.getLtypeT())
                    .setSize(fieldConfig.getSize())
                    .setRequired(fieldConfig.getRequired());
            ModelField modelField = makeRelationField(context, meta, throughModel, field, null,
                    Boolean.TRUE, Boolean.TRUE,
                    SystemSourceEnum.RELATION, throughModelFieldMap.get(field), throughModelField);
            if (!isPkId) {
                modelField.setPk(true);
                modelField.setPkIndex(baseIndex + i);
            }
            modelField.setDictionary(fieldConfig.getDictionary());
            if (!throughModelFieldMap.containsKey(field)) {
                modelFieldList.add(modelField);
                meta.addCrossingModelField(throughModel.getModule(), modelField);
                if (!newModel) {
                    log.warn("中间模型关系字段发生变更，model:" + relation.getModel() + ",field:" + relation.getField());
                }
            }
            i++;
        }
    }

    @Override
    public void makeDefaultRelationReferenceFields(Meta meta, ModelDefinition model, ModelField relation) {
        boolean isDefaultRelationFields = CollectionUtils.isEmpty(relation.getRelationFields());
        ModelConfig references = PamirsSession.getContext().getModelConfig(relation.getReferences());
        if (isDefaultRelationFields) {
            if (TtypeEnum.isRelationOne(relation.getTtype())) {
                if (null == references || CollectionUtils.isEmpty(references.getPk())
                        && !ModelTypeEnum.TRANSIENT.value().equals(model.getType().value())) {
                    throw PamirsException.construct(BASE_RELATION_NO_REFERENCE_FIELDS_NO_PK_ERROR)
                            .appendMsg(MessageFormat.format("model:{0},field:{1}", model.getModel(), relation.getField())).errThrow();
                }
                List<String> relationFields = new ArrayList<>();
                for (String pk : references.getPk()) {
                    String defaultRelationField = relation.getField() + StringUtils.capitalize(pk);
                    relationFields.add(defaultRelationField);
                }
                relation.setRelationFields(relationFields);
            } else {
                if (CollectionUtils.isEmpty(model.getPk()) && !ModelTypeEnum.TRANSIENT.value().equals(model.getType().value())) {
                    throw PamirsException.construct(BASE_RELATION_NO_RELATION_FIELDS_NO_PK_ERROR)
                            .appendMsg(MessageFormat.format("model:{0},field:{1}", model.getModel(), relation.getField())).errThrow();
                }
                if (null != relation.getInverse() && relation.getInverse()) {
                    throw PamirsException.construct(BASE_INVERSE_RELATION_NO_RELATION_FIELDS_ERROR)
                            .appendMsg(MessageFormat.format("model:{0},field:{1}", model.getModel(), relation.getField())).errThrow();
                }
                relation.setRelationFields(model.getPk());
            }
        }
        boolean isDefaultReferenceFields = CollectionUtils.isEmpty(relation.getReferenceFields());
        if (isDefaultReferenceFields) {
            if (null != relation.getInverse() && relation.getInverse()) {
                throw PamirsException.construct(BASE_INVERSE_RELATION_NO_REFERENCE_FIELDS_ERROR)
                        .appendMsg(MessageFormat.format("model:{0},field:{1}", model.getModel(), relation.getField())).errThrow();
            }
            if (null == references || CollectionUtils.isEmpty(references.getPk())
                    && !ModelTypeEnum.TRANSIENT.value().equals(model.getType().value())) {
                throw PamirsException.construct(BASE_RELATION_NO_REFERENCE_FIELDS_NO_PK_ERROR)
                        .appendMsg(MessageFormat.format("model:{0},field:{1}", model.getModel(), relation.getField())).errThrow();
            }
            if (isDefaultRelationFields) {
                if (TtypeEnum.O2M.value().equals(relation.getTtype().value())) {
                    ModelFieldConfig referenceModelField = PamirsSession.getContext().getModelField(relation.getReferences(), model.getName());
                    if (null == referenceModelField) {
                        log.warn(MessageFormat.format("警告:{0},编码:{1},模型:{2},字段:{3}",
                                BASE_RELATION_O2M_NO_REFERENCE_FIELDS_ERROR.msg(),
                                BASE_RELATION_O2M_NO_REFERENCE_FIELDS_ERROR.code() + CharacterConstants.SEPARATOR_EMPTY,
                                model.getModel(), relation.getField()));
                    }
                    List<String> referenceFields = new ArrayList<>();
                    for (String pk : model.getPk()) {
                        String defaultReferenceField = model.getName() + StringUtils.capitalize(pk);
                        referenceFields.add(defaultReferenceField);
                    }
                    relation.setReferenceFields(referenceFields);
                } else {
                    relation.setReferenceFields(references.getPk());
                }
            } else {
                relation.setReferenceFields(relation.getRelationFields());
            }
        }
    }

    @Override
    public void makeRelationFields(ComputeContext context, Meta meta, ModelDefinition model, ModelField relation) {
        List<String> relationFields = relation.getRelationFields();
        List<String> referenceFields = relation.getReferenceFields();
        int fieldIndex = 0;
        for (String relationField : relationFields) {
            if (FieldUtils.isConstantRelationFieldValue(relationField)) {
                fieldIndex++;
                continue;
            }

            ModelField selfField = meta.getModelField(model.getModel(), relationField);
            boolean existField = null != selfField;
            boolean metaCompleted = existField && selfField.isMetaCompleted();
            if (existField && !metaCompleted && !SystemSourceEnum.RELATION.equals(selfField.getSystemSource())) {
                fieldIndex++;
                continue;
            }
            ModelField referenceField = meta.getModelField(relation.getReferences(), referenceFields.get(fieldIndex));
            selfField = makeRelationField(context, meta, model, relationField, relation, Boolean.FALSE, Boolean.FALSE,
                    SystemSourceEnum.RELATION, selfField, referenceField);
            if (!existField) {
                model.getModelFields().add(selfField);
            } else if (metaCompleted) {
                FieldUtils.replaceModelField(model, selfField);
            }
            fieldIndex++;
        }
    }

    @Override
    public void makeReferenceFields(ComputeContext context, Meta meta, ModelDefinition references, ModelField relation) {
        List<String> relationFields = relation.getRelationFields();
        List<String> referenceFields = relation.getReferenceFields();
        int fieldIndex = 0;
        for (String referenceField : referenceFields) {
            String relationField = relationFields.get(fieldIndex);
            if (FieldUtils.isConstantRelationFieldValue(relationField)) {
                fieldIndex++;
                continue;
            }

            ModelField otherSideField = meta.getModelField(references.getModel(), referenceField);
            boolean existField = null != otherSideField;
            boolean metaCompleted = existField && otherSideField.isMetaCompleted();
            if (relation.getInverse()
                    || existField && !metaCompleted
                    && !SystemSourceEnum.RELATION.equals(otherSideField.getSystemSource())) {
                fieldIndex++;
                continue;
            }
            ModelField relationModelField = meta.getModelField(relation.getModel(), relationField);
            otherSideField = makeRelationField(context, meta, references, referenceField, null,
                    Boolean.FALSE, Boolean.TRUE,
                    SystemSourceEnum.RELATION, otherSideField, relationModelField);
            if (!existField) {
                references.getModelFields().add(otherSideField);
                ModelDefinition relationModel = PamirsSession.getContext().getModelConfig(relation.getModel()).getModelDefinition();
                meta.addCrossingModelField(relationModel.getModule(), otherSideField);
            } else if (metaCompleted) {
                FieldUtils.replaceModelField(references, otherSideField);
                ModelDefinition relationModel = PamirsSession.getContext().getModelConfig(relation.getModel()).getModelDefinition();
                meta.addCrossingModelField(relationModel.getModule(), otherSideField);
            }
            fieldIndex++;
        }
    }

    @Override
    public ModelField makeRelationField(ComputeContext context, Meta meta,
                                        ModelDefinition modelDefinition, String relationFieldName, ModelField relation,
                                        boolean required, boolean crossingExtend, SystemSourceEnum sourceEnum,
                                        ModelField relationField, ModelField referenceField) {
        String relationsField = null != relation ? relation.getField() : null;
        if (null == referenceField) {
            throw PamirsException.construct(BASE_RELATION_FIELD_IS_NOT_EXIST_ERROR)
                    .appendMsg("model:" + modelDefinition.getModel() + ",field:" + relationsField).errThrow();
        }
        List<String> related = null;
        TtypeEnum ttypeEnum, relatedTtypeEnum;
        if (null != relationField) {
            // 已被重新计算
            Models.modelDirective().disableMetaCompleted(relationField);
        }
        if (!TtypeEnum.isRelationType(referenceField.getTtype())) {
            if (null == relationField) {
                relationField = new ModelField();
            }
            if (TtypeEnum.isRelatedType(referenceField.getTtype().value())) {
                ttypeEnum = referenceField.getRelatedTtype();
            } else {
                ttypeEnum = referenceField.getTtype();
            }
            relatedTtypeEnum = null;
        } else {
            related = null != relationsField ? Lists.newArrayList(relationsField, referenceField.getField()) : null;
            ModelField cloneReferenceField = ObjectUtils.clone(referenceField);
            if (null != relationField) {
                BeanUtils.copyProperties(cloneReferenceField, relationField);
            } else {
                relationField = cloneReferenceField;
            }
            ttypeEnum = TtypeEnum.RELATED;
            String ttype = Spider.getDefaultExtension(TypeProcessor.class)
                    .defaultTtypeFromLtype(referenceField.getLtype(), referenceField.getLtypeT(), referenceField.getRequestSerialize());
            relatedTtypeEnum = TtypeEnum.getEnumByValue(TtypeEnum.class, ttype);
        }
        if (crossingExtend) {
            // 跨模型扩展，需要在处理完关联关系字段后，再次处理继承
            relationField.enableMetaCrossing();
        }
        relationField.setInvisible(Boolean.TRUE)
                .setTtype(ttypeEnum)
                .setSize(referenceField.getSize())
                .setRelatedTtype(relatedTtypeEnum)
                .setLtype(referenceField.getLtype())
                .setLtypeT(referenceField.getLtypeT())
                //低代码配置了字段displayName、summary，以配置为准
                .setDisplayName(StringUtils.isNotBlank(relationField.getDisplayName()) ? relationField.getDisplayName() : relationFieldName)
                .setSummary(StringUtils.isNotBlank(relationField.getSummary()) ? relationField.getSummary() : relationFieldName)
                .setLname(relationFieldName)
                .setStore(Boolean.TRUE)
                .setRequired(required)
                .setRelated(related)
                .setRelationStore(Boolean.FALSE)
                .setName(relationFieldName)
                .setField(relationFieldName)
                .setModel(modelDefinition.getModel())
                .setModelName(modelDefinition.getName())
                .setSystemSource(sourceEnum)
                .setSign(modelDefinition.getModel() + CharacterConstants.SEPARATOR_DOT + relationFieldName)
        ;
        relationField.setColumn(ModelField.generateColumn(modelDefinition, relationField));
        CommonApiFactory.getApi(ModelDefinitionComputer.class).computeField(context, meta, Lists.newArrayList(relationField));
        return relationField;
    }

    @Override
    public ModelField makeRelatedField(ModelDefinition modelDefinition, ModelField relation, String relationFieldName, SystemSourceEnum sourceEnum, ModelField referenceField) {
        ModelField newField = ObjectUtils.clone(referenceField);
        String ttype = Spider.getDefaultExtension(TypeProcessor.class)
                .defaultTtypeFromLtype(referenceField.getLtype(), referenceField.getLtypeT(), referenceField.getRequestSerialize());
        newField.setTtype(TtypeEnum.RELATED)
                .setRelatedTtype(TtypeEnum.getEnumByValue(TtypeEnum.class, ttype))
                .setColumnDefinition(referenceField.getColumnDefinition())
                .setDisplayName(newField.getDisplayName())
                .setLname(relationFieldName)
                .setStore(Boolean.FALSE)
                .setRequired(Boolean.FALSE)
                .setRelated(Lists.newArrayList(relation.getField(), referenceField.getField()))
                .setRelationStore(Boolean.FALSE)
                .setName(relationFieldName)
                .setField(relationFieldName)
                .setModel(modelDefinition.getModel())
                .setModelName(modelDefinition.getName())
                .setSystemSource(sourceEnum)
                .setSign(modelDefinition.getModel() + CharacterConstants.SEPARATOR_DOT + relationFieldName)
        ;
        newField.setColumn(ModelField.generateColumn(modelDefinition, newField));
        return newField;
    }

    @Override
    public void computeOverrideField(Meta meta, ModelField modelField, String overrideField,
                                     BiConsumer<ModelDefinition, ModelField> consumer) {
        ModelDefinition modelDefinition = meta.getModel(modelField.getModel());
        MutablePair<List<ModelField>, String> fieldPath = new MutablePair<>();
        fieldPath.setLeft(new ArrayList<>());
        fieldPath.setRight(overrideField);
        InheritedComputeTemplate.compute(meta, modelDefinition, new HashMap<>(),
                null, null, false,
                (currentModel, superModel) -> StringUtils.isNotBlank(fieldPath.getRight()),
                (currentModel, superModel) -> generateOverridePath(fieldPath, meta, superModel.getModel()),
                (currentModel, superModel) -> generateOverridePath(fieldPath, meta, superModel.getModel()),
                (currentModel, superModel) -> generateOverridePath(fieldPath, meta, superModel.getModel()),
                (currentModel, superModel) -> generateOverridePath(fieldPath, meta, superModel.getModel()),
                (currentModel, superModel) -> generateOverridePath(fieldPath, meta, superModel.getModel()),
                null);
        if (!CollectionUtils.isEmpty(fieldPath.getLeft())) {
            Collections.reverse(fieldPath.getLeft());
            int i = 0;
            ModelField parentModelField = null;
            for (ModelField overrideModelField : fieldPath.getLeft()) {
                if (0 == i) {
                    ModelDefinition rootModelDefinition = meta.getModel(overrideModelField.getModel());
                    consumer.accept(rootModelDefinition, overrideModelField);
                    parentModelField = overrideModelField;
                } else {
                    overrideField(meta, overrideModelField, parentModelField);
                }
                i++;
            }
            if (null != parentModelField) {
                overrideField(meta, modelField, parentModelField);
            }
        }
    }

    private void generateOverridePath(MutablePair<List<ModelField>, String> fieldPath, Meta meta, String superModel) {
        if (StringUtils.isBlank(fieldPath.getRight())) {
            return;
        }
        ModelField overrideField = meta.getModelField(superModel, fieldPath.getRight());
        if (null != overrideField) {
            fieldPath.getLeft().add(overrideField);
            String superField;
            if (SystemSourceEnum.isInherited(overrideField.getSystemSource())) {
                superField = fieldPath.getRight();
            } else {
                superField = (String) overrideField.getAttribute(FieldAttributeConstants.OVERRIDE_FIELD);
            }
            fieldPath.setRight(superField);
        }
    }

    @Override
    public void overrideField(Meta meta, ModelField modelField, ModelField overrideModelField) {
        if (null != modelField && null != overrideModelField) {
            modelField.setTtype(overrideModelField.getTtype())
                    .setLimit(Optional.ofNullable(modelField.getLimit()).orElse(overrideModelField.getLimit()))
                    .setDomain(Optional.ofNullable(modelField.getDomain()).filter(StringUtils::isNotBlank).orElse(overrideModelField.getDomain()))
                    .setDomainSize(Optional.ofNullable(modelField.getDomainSize()).orElse(overrideModelField.getDomainSize()))
                    .setContext(Optional.ofNullable(modelField.getContext()).orElse(overrideModelField.getContext()))
                    .setSearch(Optional.ofNullable(modelField.getSearch()).filter(StringUtils::isNotBlank).orElse(overrideModelField.getSearch()))
                    .setRelationFields(Optional.ofNullable(modelField.getRelationFields()).filter(s -> !CollectionUtils.isEmpty(s)).orElse(overrideModelField.getRelationFields()))
                    .setReferences(Optional.ofNullable(modelField.getReferences()).filter(StringUtils::isNotBlank).orElse(overrideModelField.getReferences()))
                    .setReferenceFields(Optional.ofNullable(modelField.getReferenceFields()).filter(s -> !CollectionUtils.isEmpty(s)).orElse(overrideModelField.getReferenceFields()))

                    .setPageSize(Optional.ofNullable(modelField.getPageSize()).orElse(overrideModelField.getPageSize()))
                    .setOrdering(Optional.ofNullable(modelField.getOrdering()).filter(StringUtils::isNotBlank).orElse(overrideModelField.getOrdering()))
                    .setThrough(Optional.ofNullable(modelField.getThrough()).filter(StringUtils::isNotBlank).orElse(overrideModelField.getThrough()))
                    .setThroughRelationFields(Optional.ofNullable(modelField.getThroughRelationFields()).filter(s -> !CollectionUtils.isEmpty(s)).orElse(overrideModelField.getThroughRelationFields()))
                    .setThroughReferenceFields(Optional.ofNullable(modelField.getThroughReferenceFields()).filter(s -> !CollectionUtils.isEmpty(s)).orElse(overrideModelField.getThroughReferenceFields()))

                    .setOnUpdate(Optional.ofNullable(modelField.getOnUpdate()).orElse(overrideModelField.getOnUpdate()))
                    .setOnDelete(Optional.ofNullable(modelField.getOnDelete()).orElse(overrideModelField.getOnDelete()))
                    .setInverse(Optional.ofNullable(modelField.getInverse()).orElse(overrideModelField.getInverse()))
            ;
        }
    }

}
