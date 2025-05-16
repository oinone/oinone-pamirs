package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.PrimaryFieldConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Optional;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_UN_SUPPORT_STRING_TYPE_ERROR;

/**
 * 模型字段字符型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"unused"})
@Order(102)
@Component
public class ModelFieldStringConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    @Override
    public int priority() {
        return 102;
    }

    @Override
    public Result<?> validate(ExecuteContext context, MetaNames names, Field field) {
        pro.shushi.pamirs.meta.annotation.Field.String typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.String.class);
        Result<?> result = new Result<>();
        if (null == typeAnnotation) {
            return result.error();
        }
        pro.shushi.pamirs.meta.annotation.Field fieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.class);
        String fieldType = TypeUtils.getActualType(field).getTypeName();
        if (!String.class.getName().equals(fieldType) && StringUtils.isBlank(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field::serialize).orElse(null))) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_UN_SUPPORT_STRING_TYPE_ERROR)
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
        pro.shushi.pamirs.meta.annotation.Field.String fieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.String.class);
        assert fieldAnnotation != null;
        String fieldType = TypeUtils.getActualType(field).getTypeName();
        if (!TypeUtils.isMap(fieldType)) {
            modelField.setTtype(TtypeEnum.STRING);
            int size = fieldAnnotation.size();
            boolean useDefaultValue = -1 == size;
            if (useDefaultValue) {
                if (null != modelField.getMulti() && modelField.getMulti()) {
                    modelField.setSize(TypeProcessor.DEFAULT_MULTI);
                } else {
                    modelField.setSize(TypeProcessor.DEFAULT_STRING);
                }
            } else {
                modelField.setSize(size);
            }
        }
        modelField.setMin(Optional.of(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.String::min).filter(StringUtils::isNotBlank).orElse(null))
                .setMax(Optional.of(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.String::max).filter(StringUtils::isNotBlank).orElse(null))
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
