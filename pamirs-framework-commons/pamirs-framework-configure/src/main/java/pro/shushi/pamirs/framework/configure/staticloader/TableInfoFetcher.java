package pro.shushi.pamirs.framework.configure.staticloader;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.framework.common.emnu.FwExpEnumerate;
import pro.shushi.pamirs.framework.configure.contants.NameConstants;
import pro.shushi.pamirs.framework.configure.util.InheritedUtil;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.container.StaticModelConfigContainer;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.core.data.DsApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseRelation;
import pro.shushi.pamirs.meta.base.IdRelation;
import pro.shushi.pamirs.meta.base.common.CodeRelation;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.MetaValueConstants;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.ModelUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表配置获取
 * <p>
 * 2020/6/9 5:43 下午
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 */
public class TableInfoFetcher {

    public static ModelFieldConfig fetchModelFieldConfig(Class<?> modelClazz, String fieldName) {
        String model = Models.api().getModel(modelClazz);
        ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(model)).orElse(null);
        if (null == modelConfig) {
            return null;
        }
        return modelConfig.getModelFieldConfigList().stream().filter(v -> fieldName.equals(v.getLname())).findFirst().orElse(null);
    }

    public static void initStaticModelConfig(Class<?> modelClazz) {
        String modelModel = Models.api().getModel(modelClazz);
        MetaSimulator metaSimulatorAnnotation = AnnotationUtils.getAnnotation(modelClazz, MetaSimulator.class);
        if (null != metaSimulatorAnnotation) {
            modelModel = MetaSimulator.SIMULATE_PREFIX + modelModel;
        }
        final String finalModel = modelModel;
        ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(finalModel)).orElse(null);
        if (null != modelConfig && !modelConfig.isStaticConfig()) {
            throw PamirsException.construct(FwExpEnumerate.BASE_MODEL_CONFIG_INIT_ERROR).appendMsg("model:" + modelModel).errThrow();
        }
        modelConfig = StaticModelConfigContainer.getModelConfig(modelModel);
        if (null == modelConfig) {
            // 初始化模型静态配置
            modelConfig = generateStaticModelConfig(modelClazz);
            if (null == modelConfig) {
                return;
            }
            StaticModelConfigContainer.setModelConfig(modelModel, modelConfig);
            List<ModelFieldConfig> modelFieldConfigList = new ArrayList<>();
            List<ModelFieldConfig> pkFieldConfig = new ArrayList<>();
            int autoIncrementPkNum = 0;
            for (Field field : FieldUtils.getDeclaredFieldsByClass(modelClazz)) {
                if (ArrayUtils.isEmpty(field.getDeclaredAnnotations())) {
                    continue;
                }
                ModelFieldConfig modelFieldConfig = generateStaticModelFieldConfig(modelModel, modelClazz, field, modelConfig.isOnlyBasicTypeField());
                if (null != modelFieldConfig) {
                    if (modelFieldConfig.getPk()) {
                        pkFieldConfig.add(modelFieldConfig);
                        if (KeyGeneratorEnum.AUTO_INCREMENT.value().equals(modelFieldConfig.getKeyGenerator())) {
                            autoIncrementPkNum++;
                        }
                    }
                    if (modelFieldConfig.getOptimisticLocker()) {
                        modelConfig.setOptimisticLockerField(modelFieldConfig.getField());
                    }
                    modelFieldConfigList.add(modelFieldConfig);
                }
            }
            if (autoIncrementPkNum > 1) {
                throw PamirsException.construct(FwExpEnumerate.BASE_ONLY_ONE_AUTO_INCREMENT_PK).errThrow();
            }
            if (CollectionUtils.isNotEmpty(pkFieldConfig)) {
                modelConfig.setPk(pkFieldConfig.stream().sorted(Comparator.comparing(ModelFieldConfig::getPkIndex))
                        .map(ModelFieldConfig::getField).collect(Collectors.toList()));
            }
            modelConfig.setModelFieldConfigList(modelFieldConfigList);
        }
    }

    public static ModelConfig generateStaticModelConfig(Class<?> modelClazz) {
        Model.Static modelStaticAnnotation = AnnotationUtils.getAnnotation(modelClazz, Model.Static.class);
        MetaSimulator metaSimulatorAnnotation = AnnotationUtils.getAnnotation(modelClazz, MetaSimulator.class);
        String module = null;
        String moduleAbbr = null;
        boolean onlyBasicTypeField;
        boolean isSimulator = null != metaSimulatorAnnotation;
        if (null != modelStaticAnnotation && isSimulator) {
            throw PamirsException.construct(FwExpEnumerate.BASE_META_STATIC_CONFIG_ERROR)
                    .appendMsg("class:" + modelClazz.getName()).errThrow();
        }
        if (null != modelStaticAnnotation) {
            if (StringUtils.isNotBlank(modelStaticAnnotation.module())) {
                module = modelStaticAnnotation.module();
            }
            onlyBasicTypeField = modelStaticAnnotation.onlyBasicTypeField();
            if (StringUtils.isNotBlank(modelStaticAnnotation.moduleAbbr())) {
                moduleAbbr = modelStaticAnnotation.moduleAbbr();
            }
        } else if (isSimulator) {
            if (StringUtils.isNotBlank(metaSimulatorAnnotation.module())) {
                module = metaSimulatorAnnotation.module();
            }
            onlyBasicTypeField = metaSimulatorAnnotation.onlyBasicTypeField();
            if (StringUtils.isNotBlank(metaSimulatorAnnotation.moduleAbbr())) {
                moduleAbbr = metaSimulatorAnnotation.moduleAbbr();
            }
        } else {
            return null;
        }
        if (StringUtils.isBlank(module)) {
            module = ModuleConstants.MODULE_BASE;
        }
        Model modelAnnotation = AnnotationUtils.getAnnotation(modelClazz, Model.class);
        Model.Advanced selfModelAdvancedAnnotation = AnnotationUtils.getAnnotation(modelClazz, Model.Advanced.class);
        Model.Advanced modelAdvancedAnnotation = AnnotationUtils.findAnnotation(modelClazz, Model.Advanced.class);
        Model.ChangeTableInherited changeTableInheritedAnnotation = AnnotationUtils.getAnnotation(modelClazz, Model.ChangeTableInherited.class);
        Model.Ds modelDsAnnotation = AnnotationUtils.findAnnotation(modelClazz, Model.Ds.class);
        Base baseAnnotation = AnnotationUtils.getAnnotation(modelClazz, Base.class);

        ModelConfig model = new ModelConfig();
        // 模型编码
        String modelModel = Models.api().getModel(modelClazz);
        if (isSimulator) {
            modelModel = MetaSimulator.SIMULATE_PREFIX + modelModel;
        }
        model.setModel(modelModel);
        // 处理继承
        List<String> inherited = resolveInherited(modelClazz, model.getModelDefinition(), modelAdvancedAnnotation, isSimulator);
        // 处理模型类型
        ModelTypeEnum modelType = InheritedUtil.resolveModelType(modelClazz, selfModelAdvancedAnnotation);
        // 处理是否是关系模型
        NullableBoolEnum relationship = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::relationship).orElse(null);
        if (BaseRelation.class.isAssignableFrom(modelClazz)
                || IdRelation.class.isAssignableFrom(modelClazz)
                || CodeRelation.class.isAssignableFrom(modelClazz)) {
            relationship = NullableBoolEnum.TRUE;
        }
        String name = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::name)
                .filter(StringUtils::isNotBlank).map(StringUtils::uncapitalize).orElse(PStringUtils.camelCaseFromModel(modelModel));
        String displayName = Optional.ofNullable(modelAnnotation).map(Model::displayName).filter(StringUtils::isNotBlank).orElse(name);
        String summary = Optional.ofNullable(modelAnnotation).map(Model::summary).filter(StringUtils::isNotBlank).orElse(displayName);
        String remark = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::remark).filter(StringUtils::isNotBlank).orElse(summary);
        String dsKey;
        SystemSourceEnum systemSource = Optional.ofNullable(baseAnnotation).map(Base::value).orElse(null);
        if (SystemSourceEnum.KERNEL.equals(systemSource)) {
            PamirsFrameworkSystemConfiguration pamirsFrameworkSystemConfiguration = CommonApiFactory
                    .getApi(PamirsFrameworkSystemConfiguration.class);
            dsKey = pamirsFrameworkSystemConfiguration.getOriginSystemDsKey();
        } else {
            DsApi dsApi = DsApi.get();
            String finalModelModel = modelModel;
            dsKey = Optional.ofNullable(dsApi).map(DsApi::fetchModelDsMap).map(v -> v.get(finalModelModel)).orElse(null);
            if (null == dsKey) {
                dsKey = Optional.ofNullable(modelDsAnnotation).map(Model.Ds::value).filter(StringUtils::isNotBlank).orElse(null);
            }
            if (null == dsKey) {
                String finalModule = module;
                dsKey = Optional.ofNullable(dsApi).map(DsApi::fetchModuleDsMap).map(v -> v.get(finalModule)).orElse(null);
            }
            if (null == dsKey) {
                dsKey = Optional.ofNullable(dsApi).map(DsApi::originDefaultDsKey).orElse(null);
            }
        }
        String ordering = InheritedUtil.fetchModelConfigItemByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.Advanced.class))
                        .map(Model.Advanced::ordering).filter(StringUtils::isNotBlank).orElse(null));
        List<String> indexes = fetchUnionCollectionModelConfigByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.Advanced.class))
                        .map(v -> PStringUtils.trim(v.index())).orElse(null));
        List<String> uniques = fetchUnionCollectionModelConfigByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.Advanced.class))
                        .map(v -> PStringUtils.trim(v.unique())).orElse(null));
        Boolean isRelationship = Optional.ofNullable(relationship).map(NullableBoolEnum::value).orElse(false);
        Boolean logicDelete = fetchUnionModelConfigByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.Persistence.class))
                        .map(Model.Persistence::logicDelete).orElse(null),
                (c, v) -> null != v);
        String logicDeleteColumn = fetchUnionModelConfigByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.Persistence.class))
                        .map(Model.Persistence::logicDeleteColumn).filter(StringUtils::isNotBlank).orElse(null),
                (c, v) -> null != v);
        String logicDeleteValue = fetchUnionModelConfigByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.Persistence.class))
                        .map(Model.Persistence::logicDeleteValue).filter(StringUtils::isNotBlank).orElse(null),
                (c, v) -> null != v);
        String logicNotDeleteValue = fetchUnionModelConfigByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.Persistence.class))
                        .map(Model.Persistence::logicNotDeleteValue).filter(StringUtils::isNotBlank).orElse(null),
                (c, v) -> null != v);
        Boolean underCamel = fetchUnionModelConfigByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.Persistence.class))
                        .map(Model.Persistence::underCamel).orElse(null),
                (c, v) -> null != v);
        Boolean capitalMode = fetchUnionModelConfigByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.Persistence.class))
                        .map(Model.Persistence::capitalMode).orElse(null),
                (c, v) -> null != v);
        CharsetEnum charset = fetchUnionModelConfigByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.Persistence.class))
                        .map(Model.Persistence::charset).filter(v -> !CharsetEnum.DEFAULT.equals(v)).orElse(null),
                (c, v) -> null != v);
        CollationEnum collate = fetchUnionModelConfigByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.Persistence.class))
                        .map(Model.Persistence::collate).filter(v -> !CollationEnum.DEFAULT.equals(v)).orElse(null),
                (c, v) -> null != v);

        String table = findTable(module, moduleAbbr, modelClazz, selfModelAdvancedAnnotation, dsKey);

        // 填充模型
        model.getModelDefinition().setModule(module).setModuleAbbr(moduleAbbr);
        model.setOnlyBasicTypeField(onlyBasicTypeField)
                .setLname(modelClazz.getName())
                .setName(name)
                .setDisplayName(displayName)
                .setTable(table)
                .setSummary(summary)
                .setRemark(remark)
                .setSuperModels(inherited)
                .setType(modelType)
                .setDsKey(dsKey)
                .setOrdering(ordering)
                .setRelationship(isRelationship)
                .setIndexes(indexes)
                .setUniques(uniques)
                .setPamirsTableInfo(
                        PamirsTableInfo.fetchPamirsTableInfo(
                                new ModelDefinition()
//                                        .setLogicDelete(logicDelete)
//                                        .setLogicDeleteColumn(logicDeleteColumn)
//                                        .setLogicDeleteValue(logicDeleteValue)
//                                        .setLogicNotDeleteValue(logicNotDeleteValue)
                                        .setUnderCamel(underCamel)
                                        .setCapitalMode(capitalMode)
//                                        .setCharset(charset)
//                                        .setCollate(collate)
                                        .setDsKey(dsKey)
                        )
                )
        ;

        model.setTable(ModelDefinition.generateTable(model.getModelDefinition()));

        // 换表继承
        boolean changeTableInherited = null != changeTableInheritedAnnotation;

        Annotation selfModelCodeAnnotation = modelClazz.getDeclaredAnnotation(Model.Code.class);
        Model.Code modelCodeAnnotation = AnnotationUtils.findAnnotation(modelClazz, Model.Code.class);
        String sourceModel;
        if (changeTableInherited) {
            // 换表继承需要更换序列生成配置
            sourceModel = modelModel;
        } else {
            Class<?> sourceCodeClass = ModelUtils.findAnnotationDeclaringClass(Model.Code.class, modelClazz);
            sourceModel = Models.api().getModel(sourceCodeClass);
        }
        if (null != modelCodeAnnotation) {
            SequenceConfig sequenceConfig = new SequenceConfig();
            sequenceConfig.setDisplayName(model.getDisplayName() + NameConstants.SEQUENCE_CONFIG_NAME_PREFIX);
            sequenceConfig.setModule(module);
            sequenceConfig.setPrefix(modelCodeAnnotation.prefix());
            sequenceConfig.setSuffix(modelCodeAnnotation.suffix());
            sequenceConfig.setSequence(modelCodeAnnotation.sequence());
            sequenceConfig.setSize(modelCodeAnnotation.size())
                    .setStep(modelCodeAnnotation.step())
                    .setInitial(modelCodeAnnotation.initial())
                    .setFormat(modelCodeAnnotation.format())
                    .setIsRandomStep(modelCodeAnnotation.isRandomStep())
                    .setZeroingPeriod(modelCodeAnnotation.zeroingPeriod());
            sequenceConfig.setCode(sourceModel);
            if (null == selfModelCodeAnnotation) {
                sequenceConfig.setSystemSource(SystemSourceEnum.EXTEND_INHERITED);
            } else {
                sequenceConfig.setSystemSource(modelCodeAnnotation.source());
            }
            model.setSequenceConfig(sequenceConfig);
            model.getModelDefinition().setSequenceCode(sequenceConfig.getCode());
        }

        String label = InheritedUtil.fetchModelConfigItemByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.class))
                        .map(Model::label).filter(StringUtils::isNotBlank).orElse(null));
        List<String> labelFields = InheritedUtil.fetchModelConfigCollectionByClass(modelClazz,
                c -> Optional.ofNullable(AnnotationUtils.getAnnotation(c, Model.class))
                        .map(Model::labelFields).map(PStringUtils::trim)
                        .filter(CollectionUtils::isNotEmpty).orElse(null));
        model.getModelDefinition().setLabel(label);
        model.getModelDefinition().setLabelFields(labelFields);

        model.setStaticConfig(true);
        return model;
    }

    private static List<String> resolveInherited(Class<?> modelClazz, ModelDefinition modelDefinition,
                                                 Model.Advanced modelAdvancedAnnotation, boolean isSimulator) {
        List<String> inherited = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::inherited).map(Arrays::stream)
                .map(Stream::distinct).map(v -> v.collect(Collectors.toList())).orElse(new ArrayList<>());
        Class<?> superModelClazz;
        Class<?> selfClazz;
        if (isSimulator) {
            selfClazz = modelClazz;
        } else {
            selfClazz = Optional.ofNullable(modelClazz.getSuperclass()).orElse(null);
        }
        superModelClazz = Optional.ofNullable(selfClazz).map(Class::getSuperclass).orElse(null);
        Model superModelAnnotation = Optional.ofNullable(superModelClazz).map(v -> AnnotationUtils.getAnnotation(v, Model.class)).orElse(null);
        if (null != superModelClazz && null != superModelAnnotation) {
            Model.model superModelModelAnnotation = AnnotationUtils.getAnnotation(superModelClazz, Model.model.class);
            String superModel = Optional.ofNullable(superModelModelAnnotation).map(Model.model::value).orElse(superModelClazz.getName());
            Model.Advanced superModelAdvancedAnnotation = AnnotationUtils.getAnnotation(superModelClazz, Model.Advanced.class);
            ModelTypeEnum modelType = InheritedUtil.resolveModelType(modelClazz, superModelAdvancedAnnotation);
            if (ModelTypeEnum.STORE.equals(modelType)) {
                if (!inherited.contains(superModel)) {
                    inherited.add(0, superModel);
                }
            }

            Model.MultiTable multiTableAnnotation = AnnotationUtils.getAnnotation(selfClazz, Model.MultiTable.class);
            Model.MultiTableInherited multiTableInheritedAnnotation = AnnotationUtils.getAnnotation(selfClazz, Model.MultiTableInherited.class);
            if (null != multiTableInheritedAnnotation) {
                modelDefinition.setMultiTable(superModel);
                modelDefinition.setMultiTableType(multiTableInheritedAnnotation.type());
                modelDefinition.setRedundancy(multiTableInheritedAnnotation.redundancy());
            }
            if (null != multiTableAnnotation) {
                String multiTableTypeField = Optional.of(multiTableAnnotation.typeField()).filter(StringUtils::isNoneBlank)
                        .orElse(PStringUtils.camelCaseFromModel(modelDefinition.getModel()) + FieldConstants.TYPE_APPENDIX);
                modelDefinition.setMultiTableTypeField(multiTableTypeField);
            }
        }
        return inherited;
    }

    private static String findTable(String module, String moduleAbbr, Class<?> modelClazz, Model.Advanced modelAdvancedAnnotation, String dsKey) {
        if (null == modelClazz) {
            return null;
        }
        String table = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::table).filter(StringUtils::isNotBlank).orElse(null);
        if (null == table) {
            Class<?> superClazz = modelClazz.getSuperclass();
            if (null == superClazz) {
                return null;
            }
            Model.Advanced superModelAdvanceAnnotation = AnnotationUtils.getAnnotation(superClazz, Model.Advanced.class);
            if (null != superModelAdvanceAnnotation && ModelTypeEnum.ABSTRACT.equals(superModelAdvanceAnnotation.type())
                    || InheritedUtil.isMultiTableInherited(superClazz) || InheritedUtil.isChangeTableInherited(modelClazz)) {
                String modelModel = Models.api().getModel(modelClazz);
                String modelName = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::name).filter(StringUtils::isNotBlank).orElse(null);
                if (null == modelName) {
                    modelName = PStringUtils.camelCaseFromModel(modelModel);
                }
                return ModelDefinition.generateTable(
                        new ModelDefinition().setModule(module).setModuleAbbr(moduleAbbr).setModel(modelModel).setName(modelName).setDsKey(dsKey));
            }
            table = findTable(module, moduleAbbr, superClazz, superModelAdvanceAnnotation, dsKey);
        }
        return table;
    }

    public static <T> T fetchUnionModelConfigByClass(Class<?> clazz,
                                                     Function<Class<?>, T> configFetcher,
                                                     BiFunction<Class<?>, T, Boolean> validCondition) {
        return InheritedUtil.fetchUnionModelConfigByClass(clazz, configFetcher,
                InheritedUtil::isTransientInherited,
                (c, sc) -> InheritedUtil.isMultiTableInherited(c) || InheritedUtil.isChangeTableInherited(c) || InheritedUtil.isAbstractTableInherited(c, sc),
                validCondition);
    }

    public static <T> List<T> fetchUnionCollectionModelConfigByClass(Class<?> clazz,
                                                                     Function<Class<?>, Collection<T>> configFetcher) {
        return (List<T>) InheritedUtil.fetchUnionCollectionModelConfigByClass(clazz, configFetcher,
                c -> !InheritedUtil.isTransientInherited(c), (c, v) -> !InheritedUtil.isTransientInherited(c));
    }

    public static ModelFieldConfig generateStaticModelFieldConfig(String model, Class<?> clazz, Field field, boolean onlyBasicTypeField) {
        pro.shushi.pamirs.meta.annotation.Field fieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.class);
        if (null == fieldAnnotation) {
            return null;
        }
        pro.shushi.pamirs.meta.annotation.Field.Advanced fieldAdvancedAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Advanced.class);
        pro.shushi.pamirs.meta.annotation.Field.field fieldFieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.field.class);
        String fieldField = Optional.ofNullable(fieldFieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.field::value).orElse(field.getName());
        String fieldName = Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::name).filter(StringUtils::isNotBlank).orElse(field.getName());
        String displayName = Optional.of(fieldAnnotation.displayName()).filter(StringUtils::isNotBlank).orElse(fieldName);
        boolean multi = TypeUtils.isCollection(field.getType()) || fieldAnnotation.multi();
        ModelFieldConfig modelField = new ModelFieldConfig();
        modelField.setDisplayName(displayName)
                .setLname(field.getName())
                .setColumn(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::column).filter(StringUtils::isNotBlank).orElse(null))
                .setColumnDefinition(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::columnDefinition).filter(StringUtils::isNotBlank).orElse(null))
                .setOnlyColumn(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::onlyColumn).orElse(true))
                .setInsertStrategy(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::insertStrategy).map(FieldStrategyEnum::value).orElse(FieldStrategyEnum.NOT_NULL.value()))
                .setBatchStrategy(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::batchStrategy).map(FieldStrategyEnum::value).orElse(FieldStrategyEnum.NOT_NULL.value()))
                .setUpdateStrategy(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::updateStrategy).map(FieldStrategyEnum::value).orElse(FieldStrategyEnum.NOT_NULL.value()))
                .setWhereStrategy(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::whereStrategy).map(FieldStrategyEnum::value).orElse(FieldStrategyEnum.NOT_NULL.value()))
                .setWhereCondition(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::whereCondition).filter(StringUtils::isNotBlank).orElse("%s = #{%s}"))
                .setCharset(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::charset).filter(v -> !CharsetEnum.DEFAULT.equals(v)).map(CharsetEnum::value).orElse(null))
                .setCollation(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::collate).filter(v -> !CollationEnum.DEFAULT.equals(v)).map(CollationEnum::value).orElse(null))
                .setSummary(Optional.of(fieldAnnotation.summary()).filter(StringUtils::isNotBlank).orElse(displayName))
                .setLtype(field.getType().getName())
                .setLtypeT(Optional.ofNullable(TypeUtils.getGenericType(field)).map(Type::getTypeName).orElse(null))
                .setIndex(fieldAnnotation.index())
                .setUnique(fieldAnnotation.unique())
                .setMulti(multi)
                .setPriority(Optional.of(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field::priority).orElse(100L))
                .setStore(fieldAnnotation.store().value())
                .setRequestSerialize(Optional.of(fieldAnnotation.requestSerialize()).filter(v -> !pro.shushi.pamirs.meta.annotation.Field.serialize.NON.equals(v)).filter(StringUtils::isNotBlank).orElse(null))
                .setStoreSerialize(Optional.of(fieldAnnotation.serialize()).filter(v -> !pro.shushi.pamirs.meta.annotation.Field.serialize.NON.equals(v)).filter(StringUtils::isNotBlank).orElse(null))
                .setDefaultValue(Optional.of(fieldAnnotation.defaultValue()).filter(StringUtils::isNotBlank).orElse(null))
                .setCompute(Optional.of(fieldAnnotation.compute()).filter(StringUtils::isNotBlank).orElse(null))
                .setRequired(fieldAnnotation.required())
                .setInvisible(fieldAnnotation.invisible())
                .setSource(SystemSourceEnum.BASE.value())

                .setModel(model)
                .setName(fieldName)
                .setField(fieldField)
        ;

        modelField.setColumn(ModelField.generateColumn(model, fieldName, modelField.getColumn()));

        pro.shushi.pamirs.meta.annotation.Field.Version versionFieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Version.class);
        modelField.setOptimisticLocker(null != versionFieldAnnotation);

        pro.shushi.pamirs.meta.annotation.Field.PrimaryKey primaryKeyAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.PrimaryKey.class);
        modelField.setPk(null != primaryKeyAnnotation);
        modelField.setPkIndex(Optional.ofNullable(primaryKeyAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.PrimaryKey::value).orElse(null));
        String keyGenerator = Optional.ofNullable(primaryKeyAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.PrimaryKey::keyGenerator).map(KeyGeneratorEnum::value).orElse(null);
        modelField.setKeyGenerator(keyGenerator);

        Class<?> sourceClass = field.getDeclaringClass();
        boolean isInherited = !clazz.equals(sourceClass);
        boolean isMultiTableInherited = ModelUtils.isMultiTableInherited(clazz);
        boolean isChangeTableInherited = ModelUtils.isChangeTableInherited(clazz);
        pro.shushi.pamirs.meta.annotation.Field.Sequence sequenceAnnotation = FieldUtils.findAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Sequence.class);
        String sourceModel;
        if (isChangeTableInherited || isMultiTableInherited && !isInherited) {
            sourceModel = model;
        } else {
            sourceModel = Models.api().getModel(sourceClass);
        }
        if (null != sequenceAnnotation) {
            SequenceConfig sequenceConfig = new SequenceConfig()
                    .setDisplayName(modelField.getDisplayName() + NameConstants.SEQUENCE_CONFIG_NAME_FIELD_PREFIX + model + CharacterConstants.RIGHT_BRACKET)
                    .setPrefix(sequenceAnnotation.prefix())
                    .setSuffix(sequenceAnnotation.suffix())
                    .setSequence(sequenceAnnotation.sequence())
                    .setSize(sequenceAnnotation.size())
                    .setStep(sequenceAnnotation.step())
                    .setInitial(sequenceAnnotation.initial())
                    .setFormat(sequenceAnnotation.format())
                    .setIsRandomStep(sequenceAnnotation.isRandomStep())
                    .setZeroingPeriod(sequenceAnnotation.zeroingPeriod())
                    .setCode(sourceModel + CharacterConstants.SEPARATOR_OCTOTHORPE + fieldField);
            if (isInherited) {
                sequenceConfig.setSystemSource(SystemSourceEnum.EXTEND_INHERITED);
            } else {
                sequenceConfig.setSystemSource(sequenceAnnotation.source());
            }
            modelField.setSequenceConfig(sequenceConfig);
            modelField.getModelField().setSequenceCode(sequenceConfig.getCode());
        }

        CommonApiFactory.getApi(FieldEnhanceConverter.class).convert(field, modelField.getModelField());
        if (null == modelField.getTtype()) {
            modelField.setTtype(Models.types()
                    .defaultTtypeFromLtype(modelField.getLtype(), modelField.getLtypeT(), modelField.getRequestSerialize()));
        }
        if (TtypeEnum.isRelationMany(modelField.getTtype()) && null == modelField.getPageSize()) {
            modelField.setPageSize(MetaValueConstants.pageSize);
        }

        if (multi && StringUtils.isBlank(modelField.getStoreSerialize())) {
            Class<?> actualType;
            if (TypeUtils.isCollection(modelField.getLtype())) {
                actualType = TypeUtils.getClass(modelField.getLtypeT());
            } else {
                actualType = field.getType();
            }
            if (BitEnum.class.isAssignableFrom(actualType)) {
                modelField.setStoreSerialize(SerializeEnum.BIT.value());
            }
        }

        if (StringUtils.isBlank(modelField.getRequestSerialize())
                && TtypeEnum.isStringType(modelField.getTtype())
                && (null == modelField.getMulti() || !modelField.getMulti())
                && !TypeUtils.isBaseType(field.getType())) {
            modelField.setRequestSerialize(SerializeEnum.JSON.value());
        }

        modelField.getModelField().construct0(modelField.getModelField());

        if (onlyBasicTypeField && modelField.getStore()
                && !TtypeEnum.isBasicType(modelField.getTtype()) && !TtypeEnum.ENUM.value().equals(modelField.getTtype())) {
            return null;
        }
        return modelField;
    }

}
