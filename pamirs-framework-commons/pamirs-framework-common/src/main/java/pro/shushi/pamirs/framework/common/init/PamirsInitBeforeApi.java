package pro.shushi.pamirs.framework.common.init;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 万物生SPI接口
 * <p>
 * 2022/5/10 6:20 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface PamirsInitBeforeApi {

    void preAction();

}
