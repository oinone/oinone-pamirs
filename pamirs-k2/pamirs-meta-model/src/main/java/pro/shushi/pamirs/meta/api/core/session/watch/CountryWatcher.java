package pro.shushi.pamirs.meta.api.core.session.watch;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 国家变化监听
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface CountryWatcher extends SessionWatcher<String> {

}
