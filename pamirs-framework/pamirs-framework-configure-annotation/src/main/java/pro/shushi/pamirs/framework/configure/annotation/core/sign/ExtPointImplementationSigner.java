package pro.shushi.pamirs.framework.configure.annotation.core.sign;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;

/**
 * 扩展点实例签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(ExtPointImplementation.MODEL_MODEL)
public class ExtPointImplementationSigner implements ModelSigner<ExtPointImplementation> {

    @Override
    public String sign(ExtPointImplementation metaModelObject) {
        return metaModelObject.getNamespace() + CharacterConstants.SEPARATOR_DOT + metaModelObject.getName() + CharacterConstants.SEPARATOR_OCTOTHORPE
                + metaModelObject.getExecuteNamespace() + CharacterConstants.SEPARATOR_DOT + metaModelObject.getExecuteFun();
    }

}
