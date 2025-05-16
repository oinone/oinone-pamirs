package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

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

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_UN_SUPPORT_BOOLEAN_TYPE_ERROR;

/**
 * 模型字段布尔型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Order(102)
@Component
public class ModelFieldBooleanConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    @Override
    public int priority() {
        return 102;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Field source) {
        pro.shushi.pamirs.meta.annotation.Field.Boolean typeAnnotation = AnnotationUtils.getAnnotation(source, pro.shushi.pamirs.meta.annotation.Field.Boolean.class);
        Result result = new Result();
        if (null == typeAnnotation/* && !TypeUtils.isBool(source.getType())*/) {
            return result.error();
        }
        String fieldType = TypeUtils.getActualType(source).getTypeName();
        if (!(Short.class.getName().equals(fieldType) || Integer.class.getName().equals(fieldType) || Boolean.class.getName().equals(fieldType)
                || String.class.getName().equals(fieldType))
        ) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_UN_SUPPORT_BOOLEAN_TYPE_ERROR)
                    .append(MessageFormat
                            .format("，类{0} 字段{1}的类型{2}",
                                    source.getDeclaringClass().getName(), source.getName(), source.getType().getName())));
            result.error();
            context.error();
        }
        return result;
    }

    @Override
    public ModelField convert(MetaNames names, Field source, ModelField modelField) {
        modelField.setTtype(TtypeEnum.BOOLEAN);
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
