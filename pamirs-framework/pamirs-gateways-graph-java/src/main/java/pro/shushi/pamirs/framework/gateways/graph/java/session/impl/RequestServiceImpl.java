package pro.shushi.pamirs.framework.gateways.graph.java.session.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.framework.gateways.graph.java.session.RequestService;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestResult;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.function.Supplier;

/**
 * @author Adamancy Zhang at 10:28 on 2025-08-19
 */
@Order
@SPI.Service
@Service
public class RequestServiceImpl implements RequestService {

    @Override
    public PamirsRequestResult handle(String moduleName, String resolvedModuleName, Supplier<PamirsRequestResult> executeFunction) {
        return executeFunction.get();
    }
}
