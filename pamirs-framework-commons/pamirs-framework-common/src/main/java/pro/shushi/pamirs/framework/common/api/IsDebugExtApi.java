package pro.shushi.pamirs.framework.common.api;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 是否为debug，业务自行扩展API
 * <p>
 * 2024/4/3 5:53 下午
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface IsDebugExtApi {

    boolean isDebug();

}