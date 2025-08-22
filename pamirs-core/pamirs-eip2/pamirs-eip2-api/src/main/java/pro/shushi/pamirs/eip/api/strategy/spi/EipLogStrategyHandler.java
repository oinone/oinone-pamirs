package pro.shushi.pamirs.eip.api.strategy.spi;

import org.apache.camel.Exchange;
import org.apache.poi.ss.formula.functions.T;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * @author Adamancy Zhang at 17:11 on 2025-08-16
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface EipLogStrategyHandler {

    boolean isEnabled(IEipContext<?> context, Exchange exchange);

    EipLog get(IEipContext<?> context, Exchange exchange);

    EipLog create(IEipContext<?> context, Exchange exchange);

    void updateRequestTargetData(IEipContext<?> context, Exchange exchange);

    void updateResponseData(IEipContext<?> context, Exchange exchange);

    void success(IEipContext<?> context, Exchange exchange);

    void failure(IEipContext<?> context, Exchange exchange);

    void openApiFailure(Exchange exchange, String errorMsg, String resultString);
}
