package pro.shushi.pamirs.eip.api;

import org.apache.camel.ExtendedExchange;

@FunctionalInterface
public interface IEipPagingPredict<T> {

    boolean predict(IEipContext<T> context, ExtendedExchange exchange);
}
