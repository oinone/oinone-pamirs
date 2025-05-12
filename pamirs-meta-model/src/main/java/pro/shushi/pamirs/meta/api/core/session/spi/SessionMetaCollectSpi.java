package pro.shushi.pamirs.meta.api.core.session.spi;

import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * session元数据收集扩展点SPI
 * <p>
 * 2022/4/27 4:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SessionMetaCollectSpi {

    void collect(MetaData metaData, RequestContext context);

}
