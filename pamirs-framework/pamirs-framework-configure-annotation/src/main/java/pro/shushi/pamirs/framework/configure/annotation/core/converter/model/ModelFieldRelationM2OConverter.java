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

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FIELD_UN_SUPPORT_M2O_TYPE_ERROR;

/**
 * 模型字段M2O类型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class ModelFieldRelationM2OConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    @Override
    public int priority() {
        return 102;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Field field) {
        pro.shushi.pamirs.meta.annotation.Field.many2one typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.many2one.class);
        Result result = new Result();
        String trueType = TypeUtils.getActualType(field).getTypeName();
        boolean isModelType = TypeUtils.isModelClass(trueType);
        boolean isCollection = TypeUtils.isCollection(field.getType());
        if (null == typeAnnotation || !isModelType || isCollection) {
            return result.error();
        }
        if (TypeUtils.isCollection(field.getType()) || !String.class.equals(field.getType()) && TypeUtils.isValidValueLtype(field.getType().getTypeName())) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_UN_SUPPORT_M2O_TYPE_ERROR)
                    .append(I18nUtils.getMessage("ModelFieldRelationM2OConverter.typeMismatch",
                            field.getDeclaringClass().getName(), field.getName(), field.getType().getName())));
            result.error();
            context.error();
        }
        return result;
    }

    @Override
    public ModelField convert(MetaNames names, Field field, ModelField modelField) {
        modelField.setTtype(TtypeEnum.M2O);
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
