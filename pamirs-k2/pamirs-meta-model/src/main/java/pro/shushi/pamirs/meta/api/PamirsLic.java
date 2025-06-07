package pro.shushi.pamirs.meta.api;

import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestResult;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * PamirsLic
 *
 * @author yakir on 2025/05/21 10:46.
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface PamirsLic {

    PamirsRequestResult check(String resolvedModuleName);

}
