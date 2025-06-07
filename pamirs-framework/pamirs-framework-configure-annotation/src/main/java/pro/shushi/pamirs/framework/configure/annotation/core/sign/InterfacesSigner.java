package pro.shushi.pamirs.framework.configure.annotation.core.sign;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.fun.InterfaceDefinition;

/**
 * 接口签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(InterfaceDefinition.MODEL_MODEL)
public class InterfacesSigner implements ModelSigner<InterfaceDefinition> {

    @Override
    public String sign(InterfaceDefinition metaModelObject) {
        return FunctionDefinition.sign(metaModelObject.getNamespace(), metaModelObject.getFun());
    }

}
