package pro.shushi.pamirs.meta.api.session.cache.spi;

import pro.shushi.pamirs.meta.api.enmu.UriType;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 一个请求过来，根据URI请求元数据的KEY。
 * 把缓存一把拿出来放到ThreadLocal中
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2022/8/04
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface CommonMetaDataCacheApi {

    void computeMetaData(UriType uriType, String uriUniqueName);

    void appendKeyToUri(String key);

}

