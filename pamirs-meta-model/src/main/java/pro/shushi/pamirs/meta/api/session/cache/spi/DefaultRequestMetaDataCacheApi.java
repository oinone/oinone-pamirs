package pro.shushi.pamirs.meta.api.session.cache.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 默认请求元数据缓存预热实现
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2022/8/04
 */
@Order
@Component
@SPI.Service
public class DefaultRequestMetaDataCacheApi implements RequestMetaDataCacheApi {

    @Override
    public void computeMetaData(PamirsRequestParam param) {
        // default do nothing.
    }

    @Override
    public void appendKeyToUri(String key) {
        //default do nothing.
    }

}
