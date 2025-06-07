package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.annotation.AnnotationUtils;
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

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_RELATED_CONFIG_IS_EMPTY_ERROR;

/**
 * 模型字段引用类型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Component
public class ModelFieldRelatedConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    @Override
    public int priority() {
        return 105;
    }

    @SuppressWarnings({"rawtypes", "unused"})
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Field field) {
        pro.shushi.pamirs.meta.annotation.Field.Related typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Related.class);
        Result result = new Result();
        if (null == typeAnnotation) {
            return result.error();
        }
        if (ArrayUtils.isEmpty(typeAnnotation.related())) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_RELATED_CONFIG_IS_EMPTY_ERROR)
                    .append(MessageFormat
                            .format("类{0}字段{1}的类型{2}不匹配关联关系类型",
                                    field.getDeclaringClass().getName(), field.getName(), field.getType().getName())));
            result.error();
            context.error();
        }
        return result;
    }

    @Override
    public ModelField convert(MetaNames names, Field field, ModelField modelField) {
        pro.shushi.pamirs.meta.annotation.Field.Related fieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Related.class);
        pro.shushi.pamirs.meta.annotation.Field.Related.Internal fieldInternalAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Related.Internal.class);
        assert fieldAnnotation != null;
        modelField.setRelatedTtype(modelField.getTtype());
        modelField.setRelated(Optional.of(fieldAnnotation.related()).map(Arrays::asList).orElse(null))
                .setTtype(TtypeEnum.RELATED)
        ;
        if (fieldInternalAnnotation != null) {
            modelField.setRelatedInternalStore(fieldInternalAnnotation.store());
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
