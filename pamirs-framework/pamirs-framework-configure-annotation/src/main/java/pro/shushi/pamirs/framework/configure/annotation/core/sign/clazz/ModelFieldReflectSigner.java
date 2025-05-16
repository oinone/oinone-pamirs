package pro.shushi.pamirs.framework.configure.annotation.core.sign.clazz;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 模型字段签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(ModelField.MODEL_MODEL)
public class ModelFieldReflectSigner implements ModelReflectSigner<ModelField, Field> {

    @Override
    public String sign(MetaNames names, Field field) {
        String model = names.getModel();
        pro.shushi.pamirs.meta.annotation.Field.field fieldAdvancedAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.field.class);
        String fieldName = Optional.ofNullable(fieldAdvancedAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.field::value).filter(StringUtils::isNotBlank).orElse(field.getName());
        if (StringUtils.isBlank(model) || StringUtils.isBlank(fieldName)) {
            return null;
        }
        return model + CharacterConstants.SEPARATOR_DOT + fieldName;
    }

}
