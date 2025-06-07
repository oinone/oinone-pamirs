package pro.shushi.pamirs.eip.api;

import org.apache.camel.ExtendedExchange;

@FunctionalInterface
public interface IEipPagingProcessor<T> {

    void process(IEipContext<T> context, ExtendedExchange exchange);
}
