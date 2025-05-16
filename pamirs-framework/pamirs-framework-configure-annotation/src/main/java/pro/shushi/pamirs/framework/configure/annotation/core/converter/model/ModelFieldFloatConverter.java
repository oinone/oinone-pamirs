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
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Optional;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_UN_SUPPORT_FLOAT_TYPE_ERROR;

/**
 * 模型字段浮点型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Component
@Order(102)
public class ModelFieldFloatConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    @Override
    public int priority() {
        return 102;
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public Result validate(ExecuteContext context, MetaNames names, Field field) {
        pro.shushi.pamirs.meta.annotation.Field.Float typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Float.class);
        Result result = new Result<>();
        if (null == typeAnnotation/* && !java.lang.Float.class.equals(field.getType()) && !Double.class.equals(field.getType())*/) {
            return result.error();
        }
        String fieldType = TypeUtils.getActualType(field).getTypeName();
        if (!(Double.class.getName().equals(fieldType) || Float.class.getName().equals(fieldType)
                || BigDecimal.class.getName().equals(fieldType) || String.class.getName().equals(fieldType))) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_UN_SUPPORT_FLOAT_TYPE_ERROR)
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
        pro.shushi.pamirs.meta.annotation.Field.Float fieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Float.class);
        modelField.setSize(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Float::M).orElse(null))
                .setDecimal(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Float::D).orElse(null))
                .setTtype(TtypeEnum.FLOAT)
                .setMin(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Float::min).filter(StringUtils::isNotBlank).orElse(null))
                .setMax(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Float::max).filter(StringUtils::isNotBlank).orElse(null))
        ;
        String ltype = null != modelField.getMulti() && modelField.getMulti() ? modelField.getLtypeT() : modelField.getLtype();
        TypeProcessor typeProcessor = Spider.getDefaultExtension(TypeProcessor.class);
        int size = typeProcessor.fetchDefaultSizeForFloat(ltype, modelField.getSize());
        modelField.setSize(size);
        int decimal = typeProcessor.fetchDefaultDecimal(modelField.getSize(), modelField.getDecimal());
        modelField.setDecimal(decimal);
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
