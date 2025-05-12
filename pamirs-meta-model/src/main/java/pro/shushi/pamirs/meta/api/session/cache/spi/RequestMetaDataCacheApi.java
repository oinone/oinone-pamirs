package pro.shushi.pamirs.meta.api.session.cache.spi;

import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 请求元数据缓存预加载API
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2022/8/04
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface RequestMetaDataCacheApi {

    void computeMetaData(PamirsRequestParam param);

    void appendKeyToUri(String key);

}

