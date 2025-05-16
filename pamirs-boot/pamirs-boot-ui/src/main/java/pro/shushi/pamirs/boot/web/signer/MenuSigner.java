package pro.shushi.pamirs.boot.web.signer;

import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 菜单签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(Menu.MODEL_MODEL)
public class MenuSigner implements ModelSigner<Menu> {

    @Override
    public String sign(Menu metaModelObject) {
        return Menu.sign(metaModelObject.getModule(), metaModelObject.getName());
    }

}
