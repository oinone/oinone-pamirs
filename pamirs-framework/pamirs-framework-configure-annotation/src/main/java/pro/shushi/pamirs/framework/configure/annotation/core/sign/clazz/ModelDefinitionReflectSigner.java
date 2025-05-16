package pro.shushi.pamirs.framework.configure.annotation.core.sign.clazz;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.core.orm.systems.model.DefaultModelModelApi;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

/**
 * 模型签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes"})
@Slf4j
@SPI.Service(ModelDefinition.MODEL_MODEL)
public class ModelDefinitionReflectSigner implements ModelReflectSigner<ModelDefinition, Class> {

    @Override
    public String sign(MetaNames names, Class source) {
        return DefaultModelModelApi.sign(source);
    }

}
