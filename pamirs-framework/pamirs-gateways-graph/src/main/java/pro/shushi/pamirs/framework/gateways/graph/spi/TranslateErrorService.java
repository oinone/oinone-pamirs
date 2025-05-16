package pro.shushi.pamirs.framework.gateways.graph.spi;

import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestResult;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * TranslateErrorService
 *
 * @author yakir on 2023/09/19 11:42.
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface TranslateErrorService {

    default void translateError(PamirsRequestResult result) {
        // do nothing ...
    }
}
