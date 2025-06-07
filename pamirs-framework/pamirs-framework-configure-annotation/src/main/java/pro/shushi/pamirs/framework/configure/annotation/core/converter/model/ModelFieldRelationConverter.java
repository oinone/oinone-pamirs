package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.PrimaryFieldConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.base.Empty;
import pro.shushi.pamirs.meta.common.constants.MetaValueConstants;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Optional;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_NO_REFERENCE_ERROR;
import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_UN_SUPPORT_RELATION_TYPE_ERROR;

/**
 * 模型字段关系类型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class ModelFieldRelationConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    @Override
    public int priority() {
        return 200;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Result validate(ExecuteContext context, MetaNames names, Field field) {
        Result result = new Result();
        pro.shushi.pamirs.meta.annotation.Field.Relation typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Relation.class);
        pro.shushi.pamirs.meta.annotation.Field.one2one o2oAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.one2one.class);
        pro.shushi.pamirs.meta.annotation.Field.one2many o2mAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.one2many.class);
        pro.shushi.pamirs.meta.annotation.Field.many2one m2oAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.many2one.class);
        pro.shushi.pamirs.meta.annotation.Field.many2many m2mAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.many2many.class);
        String trueType = TypeUtils.getActualType(field).getTypeName();
        boolean isModelType = TypeUtils.isModelClass(trueType);
        if (null == typeAnnotation && null == o2oAnnotation && null == o2mAnnotation && null == m2oAnnotation && null == m2mAnnotation && !isModelType) {
            return result.error();
        }
        boolean isDataMapType = TypeUtils.isDataMap(trueType);
        if (!isModelType && !isDataMapType && !String.class.getName().equals(trueType)) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_UN_SUPPORT_RELATION_TYPE_ERROR)
                    .append(MessageFormat
                            .format("类{0}字段{1}的类型{2}不匹配关联关系类型",
                                    field.getDeclaringClass().getName(), field.getName(), field.getType().getName())));
            result.error();
            context.error();
        }
        String references = Optional.ofNullable(typeAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Relation::referenceClass)
                .filter(s -> !Empty.class.equals(s)).map(Models.api()::getModel)
                .orElse(Optional.ofNullable(typeAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Relation::references).orElse(trueType));
        if ((StringUtils.isBlank(references) || Empty.class.getName().equals(references)) && !isModelType) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_NO_REFERENCE_ERROR)
                    .append(MessageFormat
                            .format("请配置@Field.Relation注解的references或referenceClass属性，因为字段的Java类型不足以推断出关联模型的编码, class:{0}, field:{1}, type:{2}",
                                    field.getDeclaringClass().getName(), field.getName(), field.getType().getName())));
            result.error();
            context.error();
        }
        return result;
    }

    @Override
    public ModelField convert(MetaNames names, Field field, ModelField modelField) {
        pro.shushi.pamirs.meta.annotation.Field.Relation fieldRelationAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Relation.class);
        String references = Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Relation::references).filter(StringUtils::isNotBlank)
                .orElse(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Relation::referenceClass)
                        .filter(s -> !Empty.class.equals(s)).map(Models.api()::getModel)
                        .orElse(FieldUtils.getReferenceModel(TypeUtils.getActualType(field).getTypeName())));
        modelField.setSize(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Relation::columnSize).orElse(null))
                .setRelationStore(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Relation::store).orElse(null))
                .setDomain(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Relation::domain).filter(StringUtils::isNotBlank).orElse(null))
                .setDomainSize(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Relation::domainSize).orElse(null))
                .setContext(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Relation::context).filter(StringUtils::isNotBlank).map(JsonUtils::parseMap).orElse(null))
                .setSearch(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Relation::search).filter(StringUtils::isNotBlank).orElse(null))
                .setRelationFields(Optional.ofNullable(fieldRelationAnnotation).map(v -> ListUtils.<String>toList(v.relationFields())).filter(s -> !CollectionUtils.isEmpty(s)).orElse(null))
                .setReferences(references)
                .setReferenceFields(Optional.ofNullable(fieldRelationAnnotation).map(v -> ListUtils.<String>toList(v.referenceFields())).filter(s -> !CollectionUtils.isEmpty(s)).orElse(null));
        if (null != modelField.getTtype() && TtypeEnum.isRelationMany(modelField.getTtype().value()) && null == modelField.getPageSize()) {
            modelField.setPageSize(MetaValueConstants.pageSize);
        }
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
