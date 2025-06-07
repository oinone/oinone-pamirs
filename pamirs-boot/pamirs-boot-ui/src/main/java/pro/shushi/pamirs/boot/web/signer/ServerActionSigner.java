package pro.shushi.pamirs.boot.web.signer;

import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 服务器动作签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings("unused")
@Slf4j
@SPI.Service(ServerAction.MODEL_MODEL)
public class ServerActionSigner implements ModelSigner<ServerAction> {

    @Override
    public String sign(ServerAction metaModelObject) {
        return ServerAction.sign(metaModelObject.getModel(), metaModelObject.getName());
    }

}
