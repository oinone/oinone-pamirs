package pro.shushi.pamirs.boot.web.signer;

import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 视图动作签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(ViewAction.MODEL_MODEL)
public class ViewActionSigner implements ModelSigner<ViewAction> {

    @Override
    public String sign(ViewAction metaModelObject) {
        return ViewAction.sign(metaModelObject.getModel(), metaModelObject.getName());
    }

}
