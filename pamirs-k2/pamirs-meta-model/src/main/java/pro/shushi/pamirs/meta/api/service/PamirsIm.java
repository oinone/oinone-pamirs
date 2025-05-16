package pro.shushi.pamirs.meta.api.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.meta.api.PamirsLic;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestResult;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * PamirsIm
 *
 * @author yakir on 2025/05/21 10:52.
 */
@Order
@Service
@SPI.Service
public class PamirsIm implements PamirsLic {

    @Override
    public PamirsRequestResult check(String resolvedModuleName) {
        return null;
    }
}
