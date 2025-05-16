package pro.shushi.pamirs.eip.api;

import org.apache.camel.ExtendedExchange;

@FunctionalInterface
public interface IEipConverter<T> {

    void convert(IEipContext<T> context, ExtendedExchange exchange);
}
