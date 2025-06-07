package pro.shushi.pamirs.boot.web.signer;

import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 视图签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(View.MODEL_MODEL)
public class ViewSigner implements ModelSigner<View> {

    @Override
    public String sign(View metaModelObject) {
        return View.sign(metaModelObject.getModel(), metaModelObject.getName());
    }

}
