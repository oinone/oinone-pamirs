package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.PrimaryFieldConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.constant.FieldAttributeConstants;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Optional;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_UN_SUPPORT_O2M_TYPE_ERROR;

/**
 * 模型字段O2M类型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class ModelFieldRelationO2MConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    @Override
    public int priority() {
        return 102;
    }

    @SuppressWarnings({"rawtypes", "unused"})
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Field field) {
        pro.shushi.pamirs.meta.annotation.Field.one2many typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.one2many.class);
        Result result = new Result();
        String trueType = TypeUtils.getActualType(field).getTypeName();
        boolean isModelType = TypeUtils.isModelClass(trueType);
        boolean isCollection = TypeUtils.isCollection(field.getType());
        if (null == typeAnnotation || !isModelType || !isCollection) {
            return result.error();
        }
        if (!TypeUtils.isCollection(field.getType()) || !String.class.equals(field.getType()) && TypeUtils.isValidValueLtype(field.getType().getTypeName())) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_UN_SUPPORT_O2M_TYPE_ERROR)
                    .append(MessageFormat.format("类{0}字段{1}的类型{2}不匹配一对多关系",
                            field.getDeclaringClass().getName(), field.getName(), field.getType().getName())));
            result.error();
            context.error();
        }
        return result;
    }

    @Override
    public ModelField convert(MetaNames names, Field field, ModelField modelField) {
        pro.shushi.pamirs.meta.annotation.Field.one2many fieldRelationAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.one2many.class);
        pro.shushi.pamirs.meta.annotation.Field.Page pageFieldRelationAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Page.class);
        modelField.setLimit(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.one2many::limit).filter(f -> -1 == f).orElse(null))
                .setPageSize(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.one2many::pageSize).orElse(null))
                .setOrdering(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.one2many::ordering).filter(StringUtils::isNotBlank).orElse(null))
                .setTtype(TtypeEnum.O2M)
                .setOnUpdate(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.one2many::onUpdate).orElse(null))
                .setOnDelete(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.one2many::onDelete).orElse(null))
                .setInverse(Optional.ofNullable(fieldRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.one2many::inverse).orElse(null))
        ;
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
