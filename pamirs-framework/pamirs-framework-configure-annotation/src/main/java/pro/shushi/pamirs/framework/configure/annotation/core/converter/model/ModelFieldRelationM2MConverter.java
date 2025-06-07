package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.PrimaryFieldConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.base.Empty;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.constant.FieldAttributeConstants;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Optional;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_M2M_NO_THROUGH_ERROR;
import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_UN_SUPPORT_M2M_TYPE_ERROR;

/**
 * 模型字段M2M类型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class ModelFieldRelationM2MConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    @Override
    public int priority() {
        return 102;
    }

    @SuppressWarnings({"rawtypes", "unused"})
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Field field) {
        pro.shushi.pamirs.meta.annotation.Field.many2many typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.many2many.class);
        Result result = new Result();
        if (null == typeAnnotation) {
            return result.error();
        }
        if (!TypeUtils.isCollection(field.getType()) || !String.class.equals(field.getType()) && TypeUtils.isValidValueLtype(field.getType().getTypeName())) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_UN_SUPPORT_M2M_TYPE_ERROR)
                    .append(MessageFormat
                            .format("类{0}字段{1}的类型{2}不匹配多对多关系",
                                    field.getDeclaringClass().getName(), field.getName(), field.getType().getName())));
            result.error();
            context.error();
        }
        // 处理模型类型
        Model.Advanced modelAdvancedAnnotation = AnnotationUtils.getAnnotation(field.getDeclaringClass(), Model.Advanced.class);
        ModelTypeEnum modelType = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::type).orElse(null);
        if (TransientModel.class.isAssignableFrom(field.getDeclaringClass()) && !TransientModel.class.equals(field.getDeclaringClass())) {
            modelType = ModelTypeEnum.TRANSIENT;
        }
        String through = Optional.of(typeAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.many2many::throughClass)
                .filter(s -> !Empty.class.equals(s)).map(Models.api()::getModel)
                .orElse(Optional.of(typeAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.many2many::through).orElse(null));
        pro.shushi.pamirs.meta.annotation.Field.Relation relationAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Relation.class);
        boolean relationStore = Optional.ofNullable(relationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Relation::store).orElse(Boolean.TRUE);
        if (!ModelTypeEnum.TRANSIENT.equals(modelType) && relationStore && (StringUtils.isBlank(through) || Empty.class.getName().equals(through))) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.INFO)
                    .msg(BASE_FIELD_M2M_NO_THROUGH_ERROR)
                    .append(MessageFormat
                            .format("建议配置多对多关系的@Field.many2many注解的through或者throughClass属性，类{0}字段{1}的类型{2}",
                                    field.getDeclaringClass().getName(), field.getName(), field.getType().getName())));
        }
        return result;
    }

    @Override
    public ModelField convert(MetaNames names, Field field, ModelField modelField) {
        pro.shushi.pamirs.meta.annotation.Field.many2many fieldRelationAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.many2many.class);
        pro.shushi.pamirs.meta.annotation.Field.Page pageFieldRelationAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Page.class);
        String through = Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.many2many::throughClass)
                .filter(s -> !Empty.class.equals(s)).map(Models.api()::getModel)
                .orElse(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.many2many::through).filter(StringUtils::isNotBlank).orElse(null));
        modelField.setLimit(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.many2many::limit).filter(f -> -1 == f).orElse(null))
                .setPageSize(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.many2many::pageSize).orElse(null))
                .setOrdering(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.many2many::ordering).filter(StringUtils::isNotBlank).orElse(null))
                .setThrough(through)
                .setThroughRelationFields(Optional.ofNullable(fieldRelationAnnotation).map(v -> ListUtils.<String>toList(v.relationFields())).filter(s -> !CollectionUtils.isEmpty(s)).orElse(null))
                .setThroughReferenceFields(Optional.ofNullable(fieldRelationAnnotation).map(v -> ListUtils.<String>toList(v.referenceFields())).filter(s -> !CollectionUtils.isEmpty(s)).orElse(null))
                .setTtype(TtypeEnum.M2M)
        ;
        Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.many2many::throughDisplayName).filter(StringUtils::isNotBlank)
                .ifPresent(displayName -> modelField.addAttribute(FieldAttributeConstants.THROUGH_DISPLAY_NAME, displayName));
        Optional.ofNullable(pageFieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Page::value)
                .ifPresent(page -> modelField.addAttribute(FieldAttributeConstants.PAGE, page));
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
