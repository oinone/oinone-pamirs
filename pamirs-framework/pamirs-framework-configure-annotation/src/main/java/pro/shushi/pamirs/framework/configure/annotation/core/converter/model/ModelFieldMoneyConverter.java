package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.PrimaryFieldConverter;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 模型字段金额类型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Order(102)
@Component
public class ModelFieldMoneyConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    @Override
    public int priority() {
        return 102;
    }

    @SuppressWarnings({"rawtypes", "unused"})
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Field field) {
        pro.shushi.pamirs.meta.annotation.Field.Money typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Money.class);
        Result result = new Result();
        if (null == typeAnnotation) {
            return result.error();
        }
        return result;
    }

    @Override
    public ModelField convert(MetaNames names, Field field, ModelField modelField) {
        pro.shushi.pamirs.meta.annotation.Field.Money fieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Money.class);
        modelField.setSize(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Money::M).orElse(null))
                .setDecimal(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Money::D).orElse(null))
                .setTtype(TtypeEnum.MONEY)
                .setMin(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Money::min).filter(StringUtils::isNotBlank).orElse(null))
                .setMax(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Money::max).filter(StringUtils::isNotBlank).orElse(null))
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
