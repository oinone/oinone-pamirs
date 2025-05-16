package pro.shushi.pamirs.eip.api;

import org.apache.camel.ExtendedExchange;

public interface IEipFilter<T> {

    boolean matches(IEipContext<T> context, ExtendedExchange exchange);
}