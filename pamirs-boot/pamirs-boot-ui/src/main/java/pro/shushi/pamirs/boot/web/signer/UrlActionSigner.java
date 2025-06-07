package pro.shushi.pamirs.boot.web.signer;

import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 链接动作签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(UrlAction.MODEL_MODEL)
public class UrlActionSigner implements ModelSigner<UrlAction> {

    @Override
    public String sign(UrlAction metaModelObject) {
        return UrlAction.sign(metaModelObject.getModel(), metaModelObject.getName());
    }

}
