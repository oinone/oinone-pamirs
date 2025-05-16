package pro.shushi.pamirs.meta.api.session.cache.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.enmu.UriType;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 一个请求过来，根据URI请求元数据的KEY。
 * 把缓存一把拿出来放到ThreadLocal中
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2022/8/04
 */
@Order
@Component
@SPI.Service
public class DefaultCommonMetaDataCacheApi implements CommonMetaDataCacheApi {

    @Override
    public void computeMetaData(UriType uriType, String uriUniqueName){
        // do nothing
    }

    @Override
    public void appendKeyToUri(String key){
        // do nothing
    }

}

