package pro.shushi.pamirs.framework.compute.system;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.ModelDefinitionComputer;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedProcessor;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.EnhanceModel;
import pro.shushi.pamirs.meta.base.K2;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;
import pro.shushi.pamirs.meta.domain.ModelData;
import pro.shushi.pamirs.meta.domain.fun.Argument;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.fun.Type;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.ModelUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 继承处理器默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultInheritedProcessor implements InheritedProcessor {

    @Override
    public List<ModelDefinition> sortModelByInherited(String module, List<ModelDefinition> modelDefinitionList) {
        if (CollectionUtils.isEmpty(modelDefinitionList)) {
            return modelDefinitionList;
        }
        List<ModelDefinition> result = new ArrayList<>();
        Map<String, ModelDefinition> modelDefinitionMap = modelDefinitionList.stream().collect(Collectors.toMap(ModelDefinition::getModel, v -> v));
        Set<String> completedSet = new HashSet<>();
        for (ModelDefinition modelDefinition : modelDefinitionList) {
            recursionModelForInherited(modelDefinition, completedSet, current -> {
                if (null != current) {
                    ModelDefinition target = modelDefinitionMap.get(current.getModel());
                    if (target != null) {
                        result.add(target);
                    }
                }
            }, (current, parent) -> {
                // 无需处理
            });
        }
        return result;
    }

    @Override
    public void recursionModelForInherited(ModelDefinition data, Set<String> completedModel,
                                           Consumer<ModelDefinition> currentConsumer,
                                           BiConsumer<ModelDefinition, ModelDefinition> parentConsumer) {
        if (null == data || completedModel.contains(data.getModel())) {
            return;
        }
        completedModel.add(data.getModel());
        // 处理继承
        List<String> inheritedList = data.getSuperModels();
        if (!CollectionUtils.isEmpty(inheritedList)) {
            for (String inherited : inheritedList) {
                // 递归处理父类
                ModelDefinition superModel = Optional.ofNullable(PamirsSession.getContext().getModelConfig(inherited))
                        .map(ModelConfig::getModelDefinition).orElse(null);
                if (null == superModel) {
                    throw PamirsException.construct(ComputeExpEnumerate.BASE_MODEL_NOT_EXISTS_ERROR)
                            .appendMsg("model:" + inherited).errThrow();
                }
                recursionModelForInherited(superModel, completedModel, currentConsumer, parentConsumer);
                parentConsumer.accept(data, superModel);
            }

        }
        currentConsumer.accept(data);
    }

    @Override
    public ModelField makeOneToOneFieldForInherited(ModelDefinition modelConfig, ModelField existModelField,
                                                    ModelDefinition superModel, List<String> pkList) {
        ModelField newRelationField;
        if (null != existModelField) {
            Models.modelDirective().disableMetaCompleted(existModelField);
            newRelationField = existModelField;
        } else {
            newRelationField = new ModelField();
        }

        String foreignKey = multiTableInheritedFieldName(superModel.getName());
        newRelationField.setStore(Boolean.FALSE)
                .setDisplayName(superModel.getDisplayName())
                .setPk(Boolean.FALSE)
                .setPriority(MetaDefaultConstants.PRIORITY_VALUE)
                .setModel(modelConfig.getModel())
                .setModelName(modelConfig.getName())
                .setRelationStore(Boolean.TRUE)
                .setName(foreignKey).setField(foreignKey)
                .setTtype(TtypeEnum.O2O)
                .setReferences(superModel.getModel())
                .setRelationFields(pkList)
                .setReferenceFields(pkList)
                .setSystemSource(SystemSourceEnum.MULTI_TABLE_INHERITED)
        ;
        return newRelationField;
    }

    @Override
    public String multiTableInheritedFieldName(String superModelName) {
        return superModelName + inheritedTag;
    }

    @Override
    public void dealProxyField(ModelDefinition modelDefinition) {
        if (CollectionUtils.isEmpty(modelDefinition.getModelFields())) {
            return;
        }
        for (ModelField field : modelDefinition.getModelFields()) {
            if (!SystemSourceEnum.isInherited(field.getSystemSource())) {
                if (TtypeEnum.isRelationType(field.getTtype())) {
                    field.setRelationStore(false);
                }
                field.setStore(false);
            }
        }
    }

    @Override
    public void dealInheritedTypeField(ComputeContext context, Meta meta,
                                       ModelDefinition currentModel, ModelDefinition superModel, SystemSourceEnum systemSourceEnum) {
        List<String> superPks = superModel.getPk();
        if (!CollectionUtils.isEmpty(superPks)) {
            // 计算多表继承父模型的类型字段
            addTypeFieldForMultiTableInherited(context, meta, superModel);
            // 增加一对一关系
            if (null == currentModel.getRedundancy() || !currentModel.getRedundancy()) {
                ModelField existModelField = meta.getModelField(currentModel.getModel(),
                        multiTableInheritedFieldName(superModel.getName()));
                boolean existField = null != existModelField;
                ModelField oneToOneField = makeOneToOneFieldForInherited(currentModel, existModelField, superModel, superPks);
                if (!existField) {
                    meta.addModelField(currentModel.getModel(), oneToOneField);
                }
            }
        } else {
            throw PamirsException.construct(ComputeExpEnumerate.BASE_MULTI_TABLE_INHERITED_NO_PKS_ERROR)
                    .appendMsg("parent model:" + superModel.getModel()).errThrow();
        }
    }

    @Override
    public void dealInheritedField(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum) {
        if (CollectionUtils.isEmpty(superModel.getModelFields())) {
            return;
        }
        for (ModelField superField : superModel.getModelFields()) {
            addField(meta, modelDefinition, superField, systemSourceEnum);
        }
    }

    @Override
    public void dealCrossingInheritedField(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum) {
        if (CollectionUtils.isEmpty(superModel.getModelFields())) {
            return;
        }
        for (ModelField superField : superModel.getModelFields()) {
            if (superField.isMetaCompleted()) {
                continue;
            }
            // 重新设置继承自跨模型继承字段的字段配置
            if (superField.isMetaCrossing()) {
                addField(meta, modelDefinition, superField, systemSourceEnum);
            }
            // 处理多对多关联关系计算继承
            if (TtypeEnum.M2M.equals(superField.getTtype())) {
                ModelField currentField = meta.getModelField(modelDefinition.getModel(), superField.getField());
                if (SystemSourceEnum.isInherited(currentField.getSystemSource())) {
                    currentField.setRelationFields(superField.getRelationFields());
                    currentField.setThroughRelationFields(superField.getThroughRelationFields());
                    currentField.setThroughReferenceFields(superField.getThroughReferenceFields());
                    currentField.setReferenceFields(superField.getReferenceFields());
                    currentField.disableMetaCompleted();
                }
            }
        }
    }

    @Override
    public void dealSuperField(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum) {
        for (ModelField modelField : modelDefinition.getModelFields()) {
            addField(meta, superModel, modelField, systemSourceEnum);
        }
    }

    @Override
    public void dealInheritedMethod(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum) {
        if (CollectionUtils.isEmpty(superModel.getFunctions())) {
            return;
        }
        // 模型是否使用默认数据管理器
        Boolean dataManagedModel = modelDefinition.getDataManager();
        for (FunctionDefinition superFunction : superModel.getFunctions()) {
            // 是否是数据管理器函数
            Boolean dataManagedFunction = superFunction.getDataManager();
            if (!dataManagedModel && dataManagedFunction && !ModelTypeEnum.ABSTRACT.equals(modelDefinition.getType())) {
                continue;
            }
            addFunction(meta, modelDefinition, superFunction, systemSourceEnum);
        }
    }

    @Override
    public void dealSuperMethod(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum) {
        if (!CollectionUtils.isEmpty(modelDefinition.getFunctions())) {
            for (FunctionDefinition function : modelDefinition.getFunctions()) {
                addFunction(meta, superModel, function, systemSourceEnum);
            }
        }
    }

    @Override
    public void dealFieldForMultiTableInherited(Meta meta, ModelDefinition self, ModelDefinition superModel, ModelField oneToOneField) {
        if (CollectionUtils.isEmpty(superModel.getModelFields())) {
            return;
        }
        List<ModelField> superFields = superModel.getModelFields();
        Set<String> fieldFilter = new HashSet<>(superModel.getPk());
        if (null != superModel.getUniques()) {
            fieldFilter.addAll(superModel.getUniques());
        }
        for (ModelField superField : superFields) {
            if (!CollectionUtils.isEmpty(self.getUnInheritedFields()) && self.getUnInheritedFields().contains(superField.getField())) {
                continue;
            }
            if (fieldFilter.contains(superField.getField()) || superField.getUnique()) {
                continue;
            }
            String field = superField.getField();
            ModelField selfField = meta.getModelField(self.getModel(), field);
            if (SystemSourceEnum.MULTI_TABLE_INHERITED.equals(selfField.getSystemSource())) {
                selfField.setRelatedTtype(selfField.getTtype());
                selfField.setTtype(TtypeEnum.RELATED);
                String superModelName = superModel.getName();
                String multiTableInheritedName = multiTableInheritedFieldName(superModelName);
                selfField.setRelated(Lists.newArrayList(multiTableInheritedName, selfField.getField()));
                selfField.setStore(false);
                selfField.setPk(false);
                selfField.setOptimisticLocker(false);
            }
        }
    }

    @Override
    public void addTypeFieldForMultiTableInherited(ComputeContext context, Meta meta, ModelDefinition modelDefinition) {
        String typeFieldName = modelDefinition.getMultiTableTypeField();
        if (StringUtils.isBlank(typeFieldName)) {
            return;
        }

        ModelField newTypeField;
        ModelField existModelField = meta.getModelField(modelDefinition.getModel(), typeFieldName);
        if (null != existModelField) {
            Models.modelDirective().disableMetaCompleted(existModelField);
            newTypeField = existModelField;
        } else {
            newTypeField = new ModelField();
        }
        newTypeField.setStore(Boolean.TRUE)
                .setDisplayName(modelDefinition.getDisplayName() + TYPE_DISPLAY_NAME)
                .setLtype(String.class.getName())
                .setSize(Spider.getDefaultExtension(TypeProcessor.class).fetchDefaultSize(TtypeEnum.STRING, String.class.getName(), false))
                .setPk(Boolean.FALSE)
                .setUnique(Boolean.FALSE)
                .setIndex(Boolean.FALSE)
                .setPriority(MetaDefaultConstants.PRIORITY_VALUE)
                .setModel(modelDefinition.getModel())
                .setModelName(modelDefinition.getName())
                .setRelationStore(Boolean.FALSE)
                .setName(typeFieldName).setField(typeFieldName)
                .setTtype(TtypeEnum.STRING)
                .setSystemSource(SystemSourceEnum.MULTI_TABLE_INHERITED)
        ;
        CommonApiFactory.getApi(ModelDefinitionComputer.class).computeField(context, meta, Lists.newArrayList(newTypeField));
        meta.placeModelField(modelDefinition.getModel(), newTypeField);
    }

    @Override
    public void addField(Meta meta, ModelDefinition self, ModelField otherField, SystemSourceEnum systemSourceEnum) {
        if (!CollectionUtils.isEmpty(self.getUnInheritedFields()) && self.getUnInheritedFields().contains(otherField.getField())) {
            return;
        }
        if (otherField.isMetaCompleted()) {
            return;
        }
        String model = self.getModel();
        ModelField selfField = meta.getModelField(model, otherField.getField());
        if (null != selfField && !selfField.isMetaCompleted()) {
            // 代理继承重载关系存储字段，子模型也需要存储关系
            if (null != otherField.getRelationStore() && otherField.getRelationStore()
                    && TtypeEnum.isRelationType(otherField.getTtype())) {
                selfField.setRelationStore(true);
            }
            if (!selfField.isMetaInherited()) {
                return;
            }
        }
        ModelField newField = ObjectUtils.clone(otherField);
        newField.disableMetaCompleted();
        if (null != selfField) {
            newField.setId(selfField.getId());
            newField.setHash(selfField.getHash());
            newField.setStringify(selfField.getStringify());
        } else {
            newField.setId(null);
            newField.setHash(null);
            newField.setStringify(null);
            newField.setModelReferences(null);
            newField.setModelThrough(null);
        }
        newField.setModel(model);
        if (null != newField.getModelDefinition()) {
            newField.getModelDefinition().setModel(model);
        }
        dealSequenceConfig(meta.getModel(model), selfField, newField);
        newField.setSys(!Boolean.FALSE.equals(self.getSys()));
        newField.setSystemSource(systemSourceEnum);
        newField.setSign(null);
        newField.setSign(newField.getSign());
        if (CollectionUtils.isEmpty(self.getModelFields())) {
            self.setModelFields(new ArrayList<>());
        }
        boolean isCrossingM2M = false;
        if (TtypeEnum.M2M.equals(otherField.getTtype())) {
            ModelDefinition otherModelDefinition = meta.getModel(otherField.getModel());
            isCrossingM2M = !self.getModule().equals(otherModelDefinition.getModule());
        }
        if (isCrossingM2M
                || meta.getCurrentModuleData().isCrossingExtendData(ModelField.MODEL_MODEL, otherField.getSign())) {
            newField.enableMetaCrossing();
        }

        MetaData currentMetaData = meta.getCurrentModuleData();
        String crossingModule = meta.getCrossingModule(ModelField.MODEL_MODEL, otherField.getSign());
        if (null != crossingModule) {
            currentMetaData.addCrossingExtendData(ModelField.MODEL_MODEL, newField.getSign(), crossingModule);
        } else {
            String oldModule = currentMetaData.removeCrossingExtendData(ModelField.MODEL_MODEL, newField.getSign());
            if (null != oldModule && !oldModule.equals(meta.getModule())) {
                currentMetaData.addDiffModelData(ModelField.MODEL_MODEL, newField.getSign(),
                        new ModelData().setModule(oldModule).setLoadModule(meta.getModule()).setLowCode(Boolean.FALSE.equals(newField.getSys()))
                                .code(ModelField.MODEL_MODEL, newField.getSign()));
            }
        }

        meta.placeModelField(model, newField);
    }

    private void dealSequenceConfig(ModelDefinition modelDefinition, ModelField selfField, ModelField newField) {
        if (null == modelDefinition || null == newField.getSequenceConfig()) {
            return;
        }
        boolean isChangeTableInherited = ModelUtils.isChangeTableInherited(modelDefinition);
        if (isChangeTableInherited) {
            String code = modelDefinition.getModel() + CharacterConstants.SEPARATOR_OCTOTHORPE + newField.getField();
            newField.setSequenceCode(code);
            SequenceConfig newSequenceConfig = newField.getSequenceConfig();
            newSequenceConfig.setCode(newField.getSequenceCode()).setSystemSource(SystemSourceEnum.EXTEND_INHERITED);
            SequenceConfig selfSequenceConfig = selfField.getSequenceConfig();
            newSequenceConfig.disableMetaCompleted();
            if (null != selfSequenceConfig) {
                newSequenceConfig.setId(selfSequenceConfig.getId());
                newSequenceConfig.setHash(selfSequenceConfig.getHash());
                newSequenceConfig.setStringify(selfSequenceConfig.getStringify());
            } else {
                newSequenceConfig.setId(null);
                newSequenceConfig.setHash(null);
                newSequenceConfig.setStringify(null);
            }
            newSequenceConfig.setSign(null);
            newSequenceConfig.setSign(newSequenceConfig.getSign());
        }
    }

    @Override
    public void addFunction(Meta meta, ModelDefinition self, FunctionDefinition otherFunction, SystemSourceEnum systemSourceEnum) {
        if (!CollectionUtils.isEmpty(self.getUnInheritedFunctions()) && self.getUnInheritedFunctions().contains(otherFunction.getFun())) {
            return;
        }
        if (otherFunction.isMetaCompleted()) {
            return;
        }
        MetaData currentMetaData = meta.getCurrentModuleData();
        FunctionDefinition selfFunction = meta.getFunction(self.getModel(), otherFunction.getFun());
        if (null != selfFunction) {
            if (!selfFunction.isMetaCompleted()) {
                //子类不允许重载父类的dataManager和category属性
                selfFunction.setDataManager(otherFunction.getDataManager());
                if (selfFunction.getDataManager()) {
                    selfFunction.setCategory(otherFunction.getCategory());
                    if (!selfFunction.getOpenLevel().contains(FunctionOpenEnum.REMOTE)) {
                        selfFunction.getOpenLevel().add(FunctionOpenEnum.REMOTE);
                        Optional.ofNullable(selfFunction.getOpenLevel()).ifPresent(v -> v.sort(Comparator.comparing(FunctionOpenEnum::value)));
                    }
                }

                convertSuperModelToCurrentModelForFunction(selfFunction.getArgumentList(), selfFunction.getReturnType(), self);
                if (StringUtils.equals(EnhanceModel.MODEL_MODEL, otherFunction.getNamespace()) && FunctionConstants.queryPage.equals(otherFunction.getFun())) {
                    log.debug("处理EnhanceModel Func");
                    selfFunction.setClazz(otherFunction.getClazz());
                    selfFunction.setBeanName(otherFunction.getBeanName());
                }
                return;
            }
        }

        FunctionDefinition newFunction = null;
        //FIXME CPC 如果自身函数非集成过来的，就不用再从父模型中拿了
        if (selfFunction != null && selfFunction.getId() != null && SystemSourceEnum.MANUAL.value().equals(selfFunction.getSystemSource().value())) {
            if (!meta.getBootModuleSet().contains(selfFunction.getModule())
                    && Boolean.TRUE.equals(selfFunction.getDataManager())) {
                if (!FunctionConstants.construct.equals(selfFunction.getFun())) {
                    newFunction = ObjectUtils.clone(selfFunction);
                } else {
                    if (selfFunction.getArgumentList() != null && selfFunction.getArgumentList().size() == 1) {
                        //construct 参数、结果一致
                        if (selfFunction.getReturnType() != null && selfFunction.getReturnType().getLtype() != null && selfFunction.getArgumentList().get(0).getLtype() != null
                                && selfFunction.getReturnType().getLtype().equals(selfFunction.getArgumentList().get(0).getLtype())) {
                            newFunction = ObjectUtils.clone(selfFunction);
                        }
                    }
                }
            }
        }
        if (newFunction == null) {
            newFunction = ObjectUtils.clone(otherFunction);
        }
        newFunction.disableMetaCompleted();
        if (null != selfFunction) {
            newFunction.setId(selfFunction.getId());
            newFunction.setHash(selfFunction.getHash());
            newFunction.setStringify(selfFunction.getStringify());
        } else {
            newFunction.setId(null);
            newFunction.setHash(null);
            newFunction.setStringify(null);
        }
        newFunction.setNamespace(self.getModel());
        newFunction.setSys(!Boolean.FALSE.equals(self.getSys()));
        newFunction.setSystemSource(systemSourceEnum);
        convertSuperModelToCurrentModelForFunction(newFunction.getArgumentList(), newFunction.getReturnType(), self);
        if (CollectionUtils.isEmpty(self.getFunctions())) {
            self.setFunctions(new ArrayList<>());
        }
        newFunction.setSign(null);
        newFunction.setSign(newFunction.getSign());

        String module;
        String crossingModule = meta.getCrossingModule(FunctionDefinition.MODEL_MODEL, otherFunction.getSign());
        if (crossingModule != null) {
            module = crossingModule;
            currentMetaData.addCrossingExtendData(FunctionDefinition.MODEL_MODEL, newFunction.getSign(), crossingModule);
        } else {
            module = self.getModule();
            String oldModule = currentMetaData.removeCrossingExtendData(FunctionDefinition.MODEL_MODEL, newFunction.getSign());
            if (null != oldModule && !oldModule.equals(meta.getModule())) {
                currentMetaData.addDiffModelData(FunctionDefinition.MODEL_MODEL, newFunction.getSign(),
                        new ModelData().setModule(oldModule).setLoadModule(module).setLowCode(Boolean.FALSE.equals(newFunction.getSys()))
                                .code(FunctionDefinition.MODEL_MODEL, newFunction.getSign()));
            }
        }
        newFunction.setModule(module);
        meta.placeFunction(self.getModel(), newFunction);
    }

    @Override
    public void convertSuperModelToCurrentModelForFunction(List<Argument> argumentList, Type returnType, ModelDefinition currentModel) {
        if (!CollectionUtils.isEmpty(argumentList)) {
            for (Argument argument : argumentList) {
                convertSuperToCurrentModel(currentModel, argument);
            }
        }
        if (null != returnType) {
            convertSuperToCurrentModel(currentModel, returnType);
        }
    }

    private void convertSuperToCurrentModel(ModelDefinition currentModel, Type type) {
        if (isSuperModel(currentModel, type.getModel()) || K2.MODEL_MODEL.equals(type.getModel())) {
            type.setModel(currentModel.getModel());
        }
    }

    @Override
    public void convertSuperModelToCurrentModelForFunction(Meta meta, List<Argument> argumentList, Type returnType, ModelDefinition currentModel) {
        if (!CollectionUtils.isEmpty(argumentList)) {
            for (Argument argument : argumentList) {
                if (isSuperModel(meta, currentModel, argument.getModel())) {
                    argument.setModel(currentModel.getModel());
                    if (null != argument.getMulti() && argument.getMulti()) {
                        argument.setLtypeT(currentModel.getLname());
                    } else {
                        argument.setLtype(currentModel.getLname());
                    }
                }
            }
        }
        if (null != returnType) {
            if (isSuperModel(meta, currentModel, returnType.getModel())) {
                returnType.setModel(currentModel.getModel());
                if (null != returnType.getMulti() && returnType.getMulti()) {
                    returnType.setLtypeT(currentModel.getLname());
                } else {
                    returnType.setLtype(currentModel.getLname());
                }
            }
        }
    }

    @Override
    public void collectAllSuperModels(List<String> superModels, ModelConfig modelConfig) {
        List<String> currentSuperModels = modelConfig.getSuperModels();
        if (!CollectionUtils.isEmpty(currentSuperModels)) {
            superModels.addAll(currentSuperModels);
            for (String superModel : currentSuperModels) {
                ModelConfig superModelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(superModel);
                collectAllSuperModels(superModels, superModelConfig);
            }
        }
    }

    @Override
    public boolean isSuperModel(Meta meta, ModelDefinition modelDefinition, String model) {
        if (StringUtils.isBlank(model)) {
            return Boolean.FALSE;
        }
        List<String> superModels = modelDefinition.getSuperModels();
        if (!CollectionUtils.isEmpty(superModels)) {
            if (superModels.contains(model)) {
                return Boolean.TRUE;
            }
            for (String superModel : superModels) {
                ModelDefinition superModelDefinition = meta.getModel(superModel);
                boolean isSuper = isSuperModel(meta, superModelDefinition, model);
                if (isSuper) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    private boolean isSuperModel(ModelDefinition modelDefinition, String model) {
        if (StringUtils.isBlank(model)) {
            return Boolean.FALSE;
        }
        List<String> superModels = modelDefinition.getSuperModels();
        if (!CollectionUtils.isEmpty(superModels)) {
            if (superModels.contains(model)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

}
