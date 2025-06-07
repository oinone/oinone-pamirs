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
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Optional;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_UN_SUPPORT_TEXT_TYPE_ERROR;

/**
 * 模型字段多行文本型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Order(102)
@Component
public class ModelFieldTextConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    @Override
    public int priority() {
        return 102;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Field field) {
        pro.shushi.pamirs.meta.annotation.Field.Text typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Text.class);
        Result result = new Result();
        if (null == typeAnnotation) {
            return result.error();
        }
        String fieldType = TypeUtils.getActualType(field).getTypeName();
        boolean isModelType = TypeUtils.isModelClass(fieldType);
        if (!String.class.getName().equals(fieldType) && !isModelType) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_UN_SUPPORT_TEXT_TYPE_ERROR)
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
        pro.shushi.pamirs.meta.annotation.Field.Text fieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Text.class);
        modelField.setTtype(TtypeEnum.TEXT)
                .setMin(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Text::min).filter(StringUtils::isNotBlank).orElse(null))
                .setMax(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Text::max).filter(StringUtils::isNotBlank).orElse(null))
                .setSize(null)
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
