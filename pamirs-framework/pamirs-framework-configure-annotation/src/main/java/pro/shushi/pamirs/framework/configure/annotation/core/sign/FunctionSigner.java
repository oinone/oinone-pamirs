package pro.shushi.pamirs.framework.configure.annotation.core.sign;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

/**
 * 函数签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(FunctionDefinition.MODEL_MODEL)
public class FunctionSigner implements ModelSigner<FunctionDefinition> {

    @Override
    public String sign(FunctionDefinition metaModelObject) {
        return FunctionDefinition.sign(metaModelObject.getNamespace(), metaModelObject.getFun());
    }

}
