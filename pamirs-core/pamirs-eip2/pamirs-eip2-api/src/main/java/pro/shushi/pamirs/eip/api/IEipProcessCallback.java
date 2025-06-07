package pro.shushi.pamirs.eip.api;

import org.apache.camel.ExtendedExchange;

@FunctionalInterface
public interface IEipProcessCallback<T> {

    void callback(IEipContext<T> context, ExtendedExchange exchange, Object object);
}
