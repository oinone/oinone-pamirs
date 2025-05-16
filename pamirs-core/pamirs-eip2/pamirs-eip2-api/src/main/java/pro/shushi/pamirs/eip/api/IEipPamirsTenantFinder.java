package pro.shushi.pamirs.eip.api;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.core.common.SuperMap;

public interface IEipPamirsTenantFinder {

    boolean match(IEipContext<SuperMap> context, ExtendedExchange exchange);

    String find(IEipContext<SuperMap> context, ExtendedExchange exchange);
}
