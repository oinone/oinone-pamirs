package pro.shushi.pamirs.framework.configure.annotation.core.sign.clazz;

import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.base.Empty;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 中间模型签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service("ModelThrough")
public class ModelThroughReflectSigner implements ModelReflectSigner<ModelDefinition, Field> {

    @Override
    public String sign(MetaNames names, Field source) {
        pro.shushi.pamirs.meta.annotation.Field.many2many fieldManyToManyRelationAnnotation = AnnotationUtils.getAnnotation(source, pro.shushi.pamirs.meta.annotation.Field.many2many.class);
        return Optional.ofNullable(fieldManyToManyRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.many2many::throughClass)
                .filter(s -> !Empty.class.equals(s)).map(Models.api()::getModel)
                .orElse(Optional.ofNullable(fieldManyToManyRelationAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.many2many::through).orElse(null));
    }

}
