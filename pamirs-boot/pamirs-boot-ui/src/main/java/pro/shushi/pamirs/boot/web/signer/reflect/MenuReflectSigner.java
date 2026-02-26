package pro.shushi.pamirs.boot.web.signer.reflect;

import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.web.utils.MenuUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 菜单签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes"})
@Slf4j
@SPI.Service(Menu.MODEL_MODEL)
public class MenuReflectSigner implements ModelReflectSigner<Menu, Class> {

    @Override
    public String sign(MetaNames names, Class source) {
        String name = MenuUtils.fetchMenuNameByAnnotation(source);
        return Menu.sign(names.getModule(), name);
    }

}
