package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.PrimaryFieldConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Optional;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_UN_SUPPORT_DATE_TYPE_ERROR;

/**
 * 模型字段时间类型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Order(102)
@Component
public class ModelFieldDateConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    private static final String WIDGET_TIMESTAMP = "timestamp";

    @Override
    public int priority() {
        return 102;
    }

    @SuppressWarnings({"rawtypes", "unused"})
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Field field) {
        pro.shushi.pamirs.meta.annotation.Field.Date typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Date.class);
        Result result = new Result();
        if (null == typeAnnotation/* && !Date.class.equals(field.getType())*/) {
            return result.error();
        }
        String fieldType = TypeUtils.getActualType(field).getTypeName();
        if (!(Date.class.getName().equals(fieldType) || java.sql.Date.class.getName().equals(fieldType)
                || java.sql.Time.class.getName().equals(fieldType) || java.sql.Timestamp.class.getName().equals(fieldType)
                || Long.class.getName().equals(fieldType) || String.class.getName().equals(fieldType))) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_UN_SUPPORT_DATE_TYPE_ERROR)
                    .append(MessageFormat
                            .format("，类{0} 字段{1}的类型{2}",
                                    field.getDeclaringClass().getName(), field.getName(), field.getType().getName())));
            result.error();
            context.error();
        }
        return result;
    }

    @Override
    public ModelField convert(MetaNames names, Field field, ModelField modelField) {
        pro.shushi.pamirs.meta.annotation.Field.Date fieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Date.class);
        modelField.setFormat(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Date::format).map(v -> DateFormatEnum.valueOf(v.name())).orElse(null))
                .setDecimal(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Date::fraction).orElse(null))
                .setTtype(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Date::type).map(v -> TtypeEnum.getEnum(TtypeEnum.class, v.name())).orElse(TtypeEnum.DATETIME))
                .setMin(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Date::min).filter(StringUtils::isNotBlank).orElse(null))
                .setMax(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Date::max).filter(StringUtils::isNotBlank).orElse(null))
        ;
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
