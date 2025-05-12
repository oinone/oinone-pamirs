package pro.shushi.pamirs.meta.api.session.cache.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;

/**
 * 扩展缓存初始化
 * <p>
 * 2022/5/5 3:44 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ExtendCacheInitApi {

    void init(Map<String, Object> extendCacheMap);

}
