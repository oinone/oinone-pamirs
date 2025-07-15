package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.configure.annotation.core.check.MetaUniqueChecker;
import pro.shushi.pamirs.framework.configure.contants.NameConstants;
import pro.shushi.pamirs.framework.configure.simulate.service.MetaSimulator;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.core.data.DsApi;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.base.BaseRelation;
import pro.shushi.pamirs.meta.base.IdRelation;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.base.common.CodeRelation;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.constant.ModelAttributeConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.ModelUtils;
import pro.shushi.pamirs.meta.util.SystemSourceUtils;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.*;

/**
 * 模型注解转化器
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class ModelDefinitionConverter implements ModelConverter<ModelDefinition, Class> {

    @Override
    public int priority() {
        return 50;
    }

    @Override
    public Result<?> validate(ExecuteContext context, MetaNames names, Class source) {
        Model modelAnnotation = AnnotationUtils.getAnnotation(source, Model.class);
        Model.model modelModelAnnotation = AnnotationUtils.getAnnotation(source, Model.model.class);
        Model.Advanced modelAdvancedAnnotation = AnnotationUtils.getAnnotation(source, Model.Advanced.class);
        Model.Static modelStaticAnnotation = AnnotationUtils.getAnnotation(source, Model.Static.class);
        Result<?> result = new Result<>();
        if (null != modelStaticAnnotation) {
            return result.error();
        }
        boolean noModelAnnotation = null == modelAnnotation;
        if (noModelAnnotation) {
            result.error();
            context.broken();
            if (null != modelModelAnnotation || null != modelAdvancedAnnotation) {
                result.addMessage(new Message().setLevel(InformationLevelEnum.WARN)
                        .msg(BASE_MODEL_NO_MODEL_ERROR)
                        .append(MessageFormat
                                .format("请为模型类配置@Model，当前模型类没有为模型类配置@Model，但是配置了@Model子注解，系统会忽略子注解配置，class:{0}，model:{1}",
                                        source.getName(), null != modelModelAnnotation ? modelModelAnnotation.value() : null)));
            } else {
                return result;
            }
        } else {
            if (0 == modelAnnotation.labelFields().length && StringUtils.isBlank(modelAnnotation.label())) {
                result.addMessage(new Message().setLevel(InformationLevelEnum.INFO)
                        .append(MessageFormat.format("label与labelFields用于前端交互的展示标题，至少设置一项，否则可能会展示为空白，class:{0}", source.getName())));
//            result.error();
            }
        }

        if (null == modelModelAnnotation) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.INFO)
                    .msg(BASE_MODEL_NO_MODEL_VALUE_INFO)
                    .append(MessageFormat
                            .format("，这样升级的时候可以不用改动此处配置，否则系统会自动给model属性填充全限定类名，升级的时候需要显式配置@Model.model且值为全限定类名，class:{0}",
                                    source.getName())));
        }

        if (StringUtils.isNotBlank(Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::table).orElse(null))) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.INFO)
                    .msg(BASE_MODEL_HAS_TABLE_INFO)
                    .append(MessageFormat
                            .format("，系统会自动给table属性填充技术名称name的驼峰转下划线表名，若需要自定义可忽略警告，class:{0}，model:{1}",
                                    source.getName(), modelAdvancedAnnotation.table())));

        }

        String[] inherited = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::inherited).orElse(null);
        if (ArrayUtils.isNotEmpty(inherited)) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.INFO)
                    .msg(BASE_MODEL_HAS_INHERITED_INFO)
                    .append(MessageFormat
                            .format("，系统会自动给inherited属性填充父类model，若需要自定义可忽略警告，class:{0}，model:{1}",
                                    source.getName(), inherited)));
        } else {
            if (null != modelAnnotation && StringUtils.isBlank(modelAnnotation.displayName())) {
                result.addMessage(new Message().setLevel(InformationLevelEnum.INFO)
                        .msg(BASE_MODEL_NO_DISPLAY_NAME_ERROR)
                        .append(MessageFormat
                                .format("建议配置@Model的displayName属性，可用于模型的显示与说明，class:{0}",
                                        source.getName())));
            }

        }

        if (!noModelAnnotation) {
            Module moduleAnnotation = AnnotationUtils.getAnnotation(source, Module.class);
            Module.module moduleModuleAnnotation = AnnotationUtils.getAnnotation(source, Module.module.class);
            Module.Advanced moduleAdvancedAnnotation = AnnotationUtils.getAnnotation(source, Module.Advanced.class);
            Fun funAnnotation = AnnotationUtils.getAnnotation(source, Fun.class);
            Dict dictAnnotation = AnnotationUtils.getAnnotation(source, Dict.class);
            if (null != moduleAnnotation || null != moduleModuleAnnotation || null != moduleAdvancedAnnotation
                    || null != funAnnotation || null != dictAnnotation) {
                result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                        .error(BASE_MODEL_CONFIG_CONFLICT_ERROR)
                        .append(MessageFormat
                                .format("请不要在模型类上配置@Module、@Module.module 、@Module.Advanced、@Fun、@Dict注解，class:{0}",
                                        source.getName())));
                context.broken().error();
                return result.error();
            }
        }

        // 模型编码重复检查
        @SuppressWarnings("unchecked")
        String modelModel = Spider.getExtension(ModelReflectSigner.class, ModelDefinition.MODEL_MODEL).sign(names, source);
        MetaUniqueChecker.check(context, result, ModelDefinition.MODEL_MODEL, modelModel, source.getName());
        return result;
    }

    @Override
    public ModelDefinition convert(MetaNames names, Class source, ModelDefinition model) {
        Model modelAnnotation = AnnotationUtils.getAnnotation(source, Model.class);
        Model.Advanced modelAdvancedAnnotation = AnnotationUtils.getAnnotation(source, Model.Advanced.class);
        Model.MultiTable multiTableAnnotation = AnnotationUtils.getAnnotation(source, Model.MultiTable.class);
        Model.MultiTableInherited multiTableInheritedAnnotation = AnnotationUtils.getAnnotation(source, Model.MultiTableInherited.class);
        Model.ChangeTableInherited changeTableInheritedAnnotation = AnnotationUtils.getAnnotation(source, Model.ChangeTableInherited.class);
        Model.Ds modelDsAnnotation = AnnotationUtils.getAnnotation(source, Model.Ds.class);
        Model.Persistence modelPersistenceAnnotation = AnnotationUtils.getAnnotation(source, Model.Persistence.class);
        // 模型编码
        @SuppressWarnings("unchecked")
        String modelModel = Spider.getExtension(ModelReflectSigner.class, ModelDefinition.MODEL_MODEL).sign(names, source);
        names.setModel(modelModel);
        // 处理继承
        List<String> inherited = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::inherited).map(Arrays::stream)
                .map(Stream::distinct).map(v -> v.collect(Collectors.toList())).orElse(new ArrayList<>());
        Class<?> superClass = source.getSuperclass();
        Model superModelAnnotation = AnnotationUtils.getAnnotation(superClass, Model.class);
        ModelTypeEnum modelType = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::type).orElse(null);
        String superModel = null;
        if (null != superModelAnnotation) {
            Model.model superModelModelAnnotation = AnnotationUtils.getAnnotation(superClass, Model.model.class);
            superModel = Optional.ofNullable(superModelModelAnnotation).map(Model.model::value).orElse(superClass.getName());
            if (!inherited.contains(superModel)) {
                inherited.add(0, superModel);
            }
            if (ModelTypeEnum.PROXY.equals(modelType)) {
                model.setProxy(ModelUtils.findProxyParent(modelModel, superClass));
            }
        }
        boolean multiTableInherited = null != multiTableInheritedAnnotation;
        if (multiTableInherited) {
            model.setMultiTable(superModel);
            model.setMultiTableType(multiTableInheritedAnnotation.type());
            model.setRedundancy(multiTableInheritedAnnotation.redundancy());
        }
        if (null != multiTableAnnotation) {
            String multiTableTypeField = Optional.of(multiTableAnnotation.typeField()).filter(StringUtils::isNoneBlank)
                    .orElse(PStringUtils.camelCaseFromModel(modelModel) + FieldConstants.TYPE_APPENDIX);
            model.setMultiTableTypeField(multiTableTypeField);
        }
        // 处理模型类型
        if (TransientModel.class.isAssignableFrom(source) && !TransientModel.class.equals(source)) {
            modelType = ModelTypeEnum.TRANSIENT;
        }
        Boolean managed = null;
        if (ModelTypeEnum.TRANSIENT.equals(modelType)) {
            managed = Boolean.FALSE;
        }
        if (null == managed) {
            managed = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::managed).orElse(true);
        }
        // 处理是否是关系模型
        NullableBoolEnum relationship = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::relationship).orElse(null);
        if (BaseRelation.class.isAssignableFrom(source)
                || IdRelation.class.isAssignableFrom(source)
                || CodeRelation.class.isAssignableFrom(source)) {
            relationship = NullableBoolEnum.TRUE;
        }
        String name = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::name).filter(StringUtils::isNotBlank)
                .map(StringUtils::uncapitalize).orElse(PStringUtils.camelCaseFromModel(modelModel));
        String displayName = Optional.ofNullable(modelAnnotation).map(Model::displayName).filter(StringUtils::isNotBlank).orElse(null);
        String table = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::table).filter(StringUtils::isNotBlank).orElse(null);
        String dsKey;
        SystemSourceEnum systemSource = SystemSourceUtils.fetch(source);
        if (SystemSourceEnum.KERNEL.equals(systemSource)) {
            PamirsFrameworkSystemConfiguration pamirsFrameworkSystemConfiguration = CommonApiFactory
                    .getApi(PamirsFrameworkSystemConfiguration.class);
            dsKey = pamirsFrameworkSystemConfiguration.getOriginSystemDsKey();
        } else {
            DsApi dsApi = DsApi.get();
            dsKey = Optional.ofNullable(dsApi.fetchModelDsMap()).map(v -> v.get(modelModel)).orElse(null);
            if (null == dsKey) {
                dsKey = Optional.ofNullable(modelDsAnnotation).map(Model.Ds::value).filter(StringUtils::isNotBlank).orElse(null);
            }
            if (null == dsKey) {
                dsKey = names.getDsKey();
            }
            if (null == dsKey) {
                if (ModuleConstants.MODULE_BASE.equals(names.getModule()) || MetaSimulator.simulate().containsKey(modelModel)) {
                    dsKey = dsApi.originSystemDsKey();
                } else {
                    dsKey = dsApi.originDefaultDsKey();
                }
            }
        }
        String summary = Optional.ofNullable(modelAnnotation).map(Model::summary).filter(StringUtils::isNotBlank).orElse(null);
        String remark = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::remark).filter(StringUtils::isNotBlank).orElse(null);
        List<String> indexes = Optional.ofNullable(modelAdvancedAnnotation).map(v -> PStringUtils.trim(v.index())).orElse(null);
        List<String> uniques = Optional.ofNullable(modelAdvancedAnnotation).map(v -> PStringUtils.trim(v.unique())).orElse(null);
        Long priority = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::priority).orElse(null);
        Boolean isRelationship = Optional.ofNullable(relationship).map(NullableBoolEnum::value).orElse(null);
        String ordering = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::ordering).filter(StringUtils::isNotBlank).orElse(null);
        boolean supportClient = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::supportClient).orElse(true);
        List<String> unInheritedFields = Optional.ofNullable(modelAdvancedAnnotation).map(v -> Arrays.asList(v.unInheritedFields())).orElse(null);
        List<String> unInheritedFunctions = Optional.ofNullable(modelAdvancedAnnotation).map(v -> Arrays.asList(v.unInheritedFunctions())).orElse(null);
        Boolean logicDelete = Optional.ofNullable(modelPersistenceAnnotation).map(Model.Persistence::logicDelete).orElse(null);
        String logicDeleteColumn = Optional.ofNullable(modelPersistenceAnnotation).map(Model.Persistence::logicDeleteColumn).filter(StringUtils::isNotBlank).orElse(null);
        String logicDeleteValue = Optional.ofNullable(modelPersistenceAnnotation).map(Model.Persistence::logicDeleteValue).filter(StringUtils::isNotBlank).orElse(null);
        String logicNotDeleteValue = Optional.ofNullable(modelPersistenceAnnotation).map(Model.Persistence::logicNotDeleteValue).filter(StringUtils::isNotBlank).orElse(null);
        Boolean underCamel = Optional.ofNullable(modelPersistenceAnnotation).map(Model.Persistence::underCamel).orElse(null);
        Boolean capitalMode = Optional.ofNullable(modelPersistenceAnnotation).map(Model.Persistence::capitalMode).orElse(null);
        CharsetEnum charset = Optional.ofNullable(modelPersistenceAnnotation).map(Model.Persistence::charset).orElse(null);
        CollationEnum collate = Optional.ofNullable(modelPersistenceAnnotation).map(Model.Persistence::collate).orElse(null);

        // 填充模型
        model.setModule(names.getModule())
                .setModuleName(names.getModuleName())
                .setDsModule(names.getModule())
                .setModuleAbbr(names.getModuleAbbr())
                .setLname(source.getName())
                .setModel(modelModel)
                .setName(name)
                .setDisplayName(displayName)
                .setTable(table)
                .setDsKey(dsKey)
                .setSummary(summary)
                .setRemark(remark)
                .setSuperModels(inherited)
                .setIndexes(indexes)
                .setUniques(uniques)
                .setType(modelType)
                .setPriority(priority)
                .setIsRelationship(isRelationship)
                .setDataManager(managed)
                .setOrdering(ordering)
                .setUnInheritedFields(unInheritedFields)
                .setUnInheritedFunctions(unInheritedFunctions)
                .setLogicDelete(logicDelete)
                .setLogicDeleteColumn(logicDeleteColumn)
                .setLogicDeleteValue(logicDeleteValue)
                .setLogicNotDeleteValue(logicNotDeleteValue)
                .setUnderCamel(underCamel)
                .setCapitalMode(capitalMode)
                .setCharset(charset)
                .setCollate(collate)
                .setStaticConfig(false)
                .setSystemSource(systemSource)
        ;

        model.removeAttribute(ModelAttributeConstants.CHANGE_TABLE_INHERITED);

        // 换表继承
        boolean changeTableInherited = null != changeTableInheritedAnnotation;
        if (changeTableInherited) {
            Optional.of(changeTableInheritedAnnotation)
                    .ifPresent(attribute -> model.addAttribute(ModelAttributeConstants.CHANGE_TABLE_INHERITED, true));
        }

        // 支持客户端
        if (!supportClient) {
            model.enableBitOption(ModelBitOptions.UN_SUPPORT_CLIENT.getOption());
        } else {
            model.disableBitOption(ModelBitOptions.UN_SUPPORT_CLIENT.getOption());
        }

        @SuppressWarnings({"unchecked"})
        Annotation selfModelCodeAnnotation = source.getDeclaredAnnotation(Model.Code.class);
        Model.Code modelCodeAnnotation = AnnotationUtils.findAnnotation(source, Model.Code.class);
        String sourceModel;
        if (changeTableInherited) {
            // 换表继承需要更换序列生成配置
            sourceModel = modelModel;
        } else {
            Class<?> sourceCodeClass = ModelUtils.findAnnotationDeclaringClass(Model.Code.class, source);
            sourceModel = Models.api().getModel(sourceCodeClass);
        }
        if (null != modelCodeAnnotation) {
            SequenceConfig sequenceConfig = new SequenceConfig();
            sequenceConfig.setDisplayName(model.getDisplayName() + NameConstants.SEQUENCE_CONFIG_NAME_PREFIX);
            sequenceConfig.setModule(names.getModule());
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
            model.setSequenceCode(sequenceConfig.getCode());
        } else {
            model.setSequenceConfig(null);
            model.setSequenceCode(null);
        }

        // 填充模型
        model.setLabelFields(PStringUtils.trim(Objects.requireNonNull(modelAnnotation).labelFields()));
        model.setLabel(Optional.of(modelAnnotation.label()).filter(StringUtils::isNotBlank).orElse(null));
        return model;
    }

    @Override
    public String group() {
        return ModelDefinition.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ModelDefinition.class;
    }

}
