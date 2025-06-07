package pro.shushi.pamirs.eip.api;


import org.apache.camel.ExtendedExchange;

@FunctionalInterface
public interface IEipIdempotentProcessor<T> {

    boolean matches(IEipContext<T> context, ExtendedExchange exchange, Object object);
}
