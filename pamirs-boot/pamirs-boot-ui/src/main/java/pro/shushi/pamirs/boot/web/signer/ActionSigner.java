package pro.shushi.pamirs.boot.web.signer;

import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 动作签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings("unused")
@Slf4j
@SPI.Service(Action.MODEL_MODEL)
public class ActionSigner implements ModelSigner<Action> {

    @Override
    public String sign(Action metaModelObject) {
        return Action.sign(metaModelObject.getModel(), metaModelObject.getName());
    }

}
