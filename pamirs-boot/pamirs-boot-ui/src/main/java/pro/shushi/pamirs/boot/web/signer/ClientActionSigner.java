package pro.shushi.pamirs.boot.web.signer;

import pro.shushi.pamirs.boot.base.model.ClientAction;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 客户端动作签名器
 *
 * @author Adamancy Zhang at 18:40 on 2021-12-06
 */
@Slf4j
@SPI.Service(ClientAction.MODEL_MODEL)
public class ClientActionSigner implements ModelSigner<ClientAction> {

    @Override
    public String sign(ClientAction metaModelObject) {
        return ClientAction.sign(metaModelObject.getModel(), metaModelObject.getName());
    }
}
