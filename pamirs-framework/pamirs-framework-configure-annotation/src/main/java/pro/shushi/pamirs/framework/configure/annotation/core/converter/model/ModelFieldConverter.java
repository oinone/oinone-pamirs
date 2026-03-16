package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.configure.annotation.core.cache.FieldMetaCache;
import pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate;
import pro.shushi.pamirs.framework.configure.contants.NameConstants;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.FieldAttributeConstants;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.SystemSourceUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_TYPE_CONFLICT_ERROR;
import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_UN_SUPPORT_TYPE_ERROR;

/**
 * 模型字段注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@Component
public class ModelFieldConverter implements ModelConverter<ModelField, Field> {

    private static final Set<Class<?>> conflictAnnotationClass = new HashSet<>();

    static {
        conflictAnnotationClass.add(pro.shushi.pamirs.meta.annotation.Field.class);
        conflictAnnotationClass.add(pro.shushi.pamirs.meta.annotation.Field.Related.class);
        conflictAnnotationClass.add(pro.shushi.pamirs.meta.annotation.Field.Related.Internal.class);
        conflictAnnotationClass.add(pro.shushi.pamirs.meta.annotation.Field.Relation.class);
        conflictAnnotationClass.add(pro.shushi.pamirs.meta.annotation.Field.field.class);
        conflictAnnotationClass.add(pro.shushi.pamirs.meta.annotation.Field.Advanced.class);
        conflictAnnotationClass.add(pro.shushi.pamirs.meta.annotation.Field.Version.class);
        conflictAnnotationClass.add(pro.shushi.pamirs.meta.annotation.Field.PrimaryKey.class);
        conflictAnnotationClass.add(pro.shushi.pamirs.meta.annotation.Field.Override.class);
        conflictAnnotationClass.add(pro.shushi.pamirs.meta.annotation.Field.Page.class);
        conflictAnnotationClass.add(pro.shushi.pamirs.meta.annotation.Field.Sequence.class);
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result<?> validate(ExecuteContext context, MetaNames names, Field field) {
        pro.shushi.pamirs.meta.annotation.Field fieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.class);
        Result<?> result = new Result<>();
        if (null == fieldAnnotation) {
            context.broken();
            return result.error();
        }
        if (!TypeUtils.isValidLtypeT(TypeUtils.getActualType(field))) {
            result.error();
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_UN_SUPPORT_TYPE_ERROR)
                    .append(I18nUtils.getMessage("ModelFieldConverter.unsupportedType",
                            field.getDeclaringClass().getName(), field.getName(), TypeUtils.getActualType(field).getTypeName())));
            context.error().broken();
        }
        boolean conflict = false;
        for (Annotation annotation : field.getAnnotations()) {
            if (annotation.annotationType().getName().startsWith(pro.shushi.pamirs.meta.annotation.Field.class.getName())
                    && !conflictAnnotationClass.contains(annotation.annotationType())) {
                if (!conflict) {
                    conflict = true;
                } else {
                    result.error();
                    result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                            .error(BASE_FIELD_TYPE_CONFLICT_ERROR)
                            .append(I18nUtils.getMessage("ModelFieldConverter.conflictingTypeAnnotations",
                                    field.getDeclaringClass().getName(), field.getName(), annotation.annotationType().getName())));
                    context.error().broken();
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public ModelField convert(MetaNames names, Field field, ModelField modelField) {
        String model = names.getModel();
        pro.shushi.pamirs.meta.annotation.Field fieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.class);
        assert fieldAnnotation != null;
        pro.shushi.pamirs.meta.annotation.Field.Advanced fieldAdvancedAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Advanced.class);
        pro.shushi.pamirs.meta.annotation.Field.field fieldFieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.field.class);
        pro.shushi.pamirs.meta.annotation.Field.Override fieldOverrideAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Override.class);
        SystemSourceEnum systemSource = SystemSourceUtils.fetch(field);
        String fieldField = Optional.ofNullable(fieldFieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.field::value).orElse(field.getName());
        String fieldName = Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::name).filter(StringUtils::isNotBlank).orElse(field.getName());
        boolean multi = FieldUtils.isMulti(field, fieldAnnotation);
        modelField.setDisplayName(I18nUtils.translateField(names.getModule(), model, fieldName, "displayName", StringUtils.defaultIfBlank(fieldAnnotation.displayName(), fieldName)))
                .setSummary(I18nUtils.translateField(names.getModule(), model, fieldName, "summary", StringUtils.defaultIfBlank(fieldAnnotation.summary(), modelField.getDisplayName())))
                .setLname(field.getName())
                .setColumn(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::column).filter(StringUtils::isNotBlank).orElse(null))
                .setColumnDefinition(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::columnDefinition).filter(StringUtils::isNotBlank).map(String::trim).orElse(null))
                .setOnlyColumn(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::onlyColumn).orElse(null))
                .setInsertStrategy(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::insertStrategy).orElse(null))
                .setBatchStrategy(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::batchStrategy).orElse(null))
                .setUpdateStrategy(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::updateStrategy).orElse(null))
                .setWhereStrategy(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::whereStrategy).orElse(null))
                .setWhereCondition(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::whereCondition).filter(StringUtils::isNotBlank).orElse(null))
                .setCharset(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::charset).filter(v -> !CharsetEnum.DEFAULT.equals(v)).orElse(null))
                .setCollation(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::collate).filter(v -> !CollationEnum.DEFAULT.equals(v)).orElse(null))
                .setLtype(field.getType().getName())
                .setLtypeT(Optional.ofNullable(TypeUtils.getGenericType(field)).map(Type::getTypeName).orElse(null))
                .setMulti(multi)
                .setPriority(Optional.of(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field::priority).filter(v -> v != MetaDefaultConstants.FAKE_PRIORITY_VALUE).orElse(MetaDefaultConstants.PRIORITY_VALUE + FieldMetaCache.getFieldSlot(field)))
                .setStore(fieldAnnotation.store().value())
                .setRequestSerialize(Optional.of(fieldAnnotation.requestSerialize()).filter(v -> !pro.shushi.pamirs.meta.annotation.Field.serialize.NON.equals(v)).filter(StringUtils::isNotBlank).orElse(null))
                .setStoreSerialize(Optional.of(fieldAnnotation.serialize()).filter(v -> !pro.shushi.pamirs.meta.annotation.Field.serialize.NON.equals(v)).filter(StringUtils::isNotBlank).orElse(null))
                .setDefaultValue(I18nUtils.translateField(names.getModule(), model, fieldName, "defaultValue", StringUtils.defaultIfBlank(fieldAnnotation.defaultValue(), null)))
                .setCompute(StringUtils.defaultIfBlank(fieldAnnotation.compute(), null))
                .setRequired(fieldAnnotation.required())
                .setImmutable(fieldAnnotation.immutable())
                .setIndex(fieldAnnotation.index())
                .setUnique(fieldAnnotation.unique())
                .setTranslate(fieldAnnotation.translate())
                .setTrack(fieldAnnotation.track())
                .setInvisible(fieldAnnotation.invisible())
                .setCopied(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::copied).orElse(null))
                .setSudo(Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::sudo).map(PStringUtils::trim).orElse(null))
                .setSize(null)
                .setLimit(null)
                .setDecimal(null)
                .setMin(null)
                .setMax(null)
                .setFormat(null)
                .setDictionary(null)
                .setOptions(null)
                .setRelated(null)
                .setRelatedTtype(null)
                .setRelationStore(null)
                .setDomain(null)
                .setDomainSize(null)
                .setContext(null)
                .setSearch(null)
                .setRelationFields(null)
                .setReferences(null)
                .setReferenceFields(null)
                .setThrough(null)
                .setThroughRelationFields(null)
                .setThroughReferenceFields(null)
                .setPageSize(null)
                .setOrdering(null)
                .setOnUpdate(null)
                .setOnDelete(null)
                .setModel(model)
                .setName(fieldName)
                .setField(fieldField)
                .setTtype(TtypeEnum.getEnumByValue(TtypeEnum.class, Models.types()
                        .defaultTtypeFromLtype(modelField.getLtype(), modelField.getLtypeT(), modelField.getRequestSerialize())))
                .setInverse(false)
                .setSystemSource(systemSource);

        // 计算字段长度
        TypeProcessor typeProcessor = Spider.getDefaultExtension(TypeProcessor.class);
        Integer size = typeProcessor.fetchDefaultSize(modelField.getTtype(), modelField.getLtype(), modelField.getMulti());
        if (null != size) {
            modelField.setSize(size);
        }

        modelField.removeAttribute(FieldAttributeConstants.OVERRIDE_FIELD);
        modelField.removeAttribute(FieldAttributeConstants.PAGE);
        modelField.removeAttribute(FieldAttributeConstants.THROUGH_DISPLAY_NAME);

        // 支持客户端
        boolean supportClient = Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Advanced::supportClient).orElse(true);
        if (!supportClient) {
            modelField.enableBitOption(FieldBitOptions.UN_SUPPORT_CLIENT.getOption());
        } else {
            modelField.disableBitOption(FieldBitOptions.UN_SUPPORT_CLIENT.getOption());
        }

        if (StringUtils.isBlank(modelField.getRequestSerialize())
                && TtypeEnum.isStringType(modelField.getTtype().value())
                && (null == modelField.getMulti() || !modelField.getMulti())
                && !TypeUtils.isBaseType(field.getType())) {
            modelField.setRequestSerialize(SerializeEnum.JSON.value());
        }

        pro.shushi.pamirs.meta.annotation.Field.Version versionFieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Version.class);
        modelField.setOptimisticLocker(null != versionFieldAnnotation);

        pro.shushi.pamirs.meta.annotation.Field.PrimaryKey primaryKeyAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.PrimaryKey.class);
        modelField.setPk(null != primaryKeyAnnotation);
        modelField.setPkIndex(Optional.ofNullable(primaryKeyAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.PrimaryKey::value).orElse(null));
        KeyGeneratorEnum keyGenerator = Optional.ofNullable(primaryKeyAnnotation)
                .filter(v -> !KeyGeneratorEnum.NON.equals(v.keyGenerator()))
                .map(pro.shushi.pamirs.meta.annotation.Field.PrimaryKey::keyGenerator).orElse(null);
        modelField.setKeyGenerator(keyGenerator);

        pro.shushi.pamirs.meta.annotation.Field.Sequence sequenceAnnotation = FieldUtils.findAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Sequence.class);
        if (null != sequenceAnnotation) {
            SequenceConfig sequenceConfig = new SequenceConfig()
                    .setDisplayName(I18nUtils.getMessage(NameConstants.SEQUENCE_CONFIG_NAME_FIELD_PREFIX, modelField.getDisplayName(), model))
                    .setModule(names.getModule())
                    .setPrefix(sequenceAnnotation.prefix())
                    .setSuffix(sequenceAnnotation.suffix())
                    .setSequence(sequenceAnnotation.sequence())
                    .setSize(sequenceAnnotation.size())
                    .setStep(sequenceAnnotation.step())
                    .setInitial(sequenceAnnotation.initial())
                    .setFormat(sequenceAnnotation.format())
                    .setIsRandomStep(sequenceAnnotation.isRandomStep())
                    .setZeroingPeriod(sequenceAnnotation.zeroingPeriod())
                    .setCode(model + CharacterConstants.SEPARATOR_OCTOTHORPE + fieldField);
            sequenceConfig.setSystemSource(sequenceAnnotation.source());
            modelField.setSequenceConfig(sequenceConfig);
            modelField.setSequenceCode(sequenceConfig.getCode());
        } else {
            modelField.setSequenceConfig(null);
            modelField.setSequenceCode(null);
        }

        Optional.ofNullable(fieldOverrideAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Override::value).filter(StringUtils::isNotBlank)
                .ifPresent(overrideField -> {
                    modelField.addAttribute(FieldAttributeConstants.OVERRIDE_FIELD, overrideField);
                    if (!TtypeEnum.isRelationType(modelField.getTtype())) {
                        throw PamirsException.construct(AnnotationExpEnumerate.BASE_OVERRIDE_FIELD_ONLY_SUPPORT_RELATION_ERROR)
                                .appendMsg("model:" + modelField.getModel() + ",field:" + modelField.getField()).errThrow();
                    }
                });

        return modelField;
    }

    @Override
    public String group() {
        return ModelField.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ModelField.class;
    }

}
