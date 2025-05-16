package pro.shushi.pamirs.framework.configure.annotation.core.sign;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.Hook;

/**
 * 拦截器签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(Hook.MODEL_MODEL)
public class HookSigner implements ModelSigner<Hook> {

    @Override
    public String sign(Hook metaModelObject) {
        return metaModelObject.getExecuteNamespace() + CharacterConstants.SEPARATOR_DOT + metaModelObject.getExecuteFun();
    }

}
