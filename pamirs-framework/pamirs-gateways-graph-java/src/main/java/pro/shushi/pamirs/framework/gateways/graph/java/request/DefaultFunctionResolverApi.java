package pro.shushi.pamirs.framework.gateways.graph.java.request;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.spi.FunctionResolverApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * @author yeshenyue on 2025/12/16 19:51.
 */
@Order
@Component
@SPI.Service
public class DefaultFunctionResolverApi implements FunctionResolverApi {

    @Override
    public Function resolveFunction(String funNamespace, String funName) {
        return PamirsSession.getContext().getFunctionByName(funNamespace, funName);
    }
}
