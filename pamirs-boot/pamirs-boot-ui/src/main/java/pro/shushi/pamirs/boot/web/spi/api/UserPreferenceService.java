package pro.shushi.pamirs.boot.web.spi.api;

import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * @author Adamancy Zhang
 * @date 2021-01-11 11:55
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface UserPreferenceService {

    /**
     * 获取当前语言编码
     *
     * @return 语言编码
     */
    String load(ViewAction viewAction, View view);

}
