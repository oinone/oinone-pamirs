package pro.shushi.pamirs.framework.configure.annotation.core.sign.clazz;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ErrorsDefinition;

/**
 * 错误组签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings("rawtypes")
@Slf4j
@SPI.Service(ErrorsDefinition.MODEL_MODEL)
public class ErrorsReflectSigner implements ModelReflectSigner<ErrorsDefinition, Class> {

    @Override
    public String sign(MetaNames names, Class source) {
        return source.getName();
    }

}
