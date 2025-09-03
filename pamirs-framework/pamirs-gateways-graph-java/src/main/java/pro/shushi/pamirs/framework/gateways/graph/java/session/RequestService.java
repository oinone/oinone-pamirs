package pro.shushi.pamirs.framework.gateways.graph.java.session;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestResult;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.function.Supplier;

/**
 * 动态请求服务
 *
 * @author Adamancy Zhang at 10:21 on 2025-08-19
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface RequestService {

    PamirsRequestResult handle(String moduleName, String resolvedModuleName, Supplier<PamirsRequestResult> executeFunction);

}
