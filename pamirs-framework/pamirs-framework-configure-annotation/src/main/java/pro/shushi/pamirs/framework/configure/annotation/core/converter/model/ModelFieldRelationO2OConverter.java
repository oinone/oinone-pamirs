package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
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

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_UN_SUPPORT_O2O_TYPE_ERROR;

/**
 * 模型字段O2O类型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class ModelFieldRelationO2OConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    @Override
    public int priority() {
        return 102;
    }

    @SuppressWarnings({"rawtypes", "unused"})
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Field field) {
        pro.shushi.pamirs.meta.annotation.Field.one2one typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.one2one.class);
        Result result = new Result();
        if (null == typeAnnotation) {
            return result.error();
        }
        if (TypeUtils.isCollection(field.getType()) || !String.class.equals(field.getType()) && TypeUtils.isValidValueLtype(field.getType().getTypeName())) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_UN_SUPPORT_O2O_TYPE_ERROR)
                    .append(I18nUtils.getMessage("ModelFieldRelationO2OConverter.typeMismatch",
                            field.getDeclaringClass().getName(), field.getName(), field.getType().getName())));
            result.error();
            context.error();
        }
        return result;
    }

    @Override
    public ModelField convert(MetaNames names, Field field, ModelField modelField) {
        pro.shushi.pamirs.meta.annotation.Field.one2one typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.one2one.class);
        modelField.setTtype(TtypeEnum.O2O);
        modelField.setOnUpdate(Optional.ofNullable(typeAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.one2one::onUpdate).orElse(null))
                .setOnDelete(Optional.ofNullable(typeAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.one2one::onDelete).orElse(null))
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
