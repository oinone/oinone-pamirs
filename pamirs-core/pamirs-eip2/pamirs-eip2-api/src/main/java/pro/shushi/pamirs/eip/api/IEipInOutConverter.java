package pro.shushi.pamirs.eip.api;

import org.apache.camel.ExtendedExchange;

@FunctionalInterface
public interface IEipInOutConverter {

    Object exchangeObject(ExtendedExchange exchange, Object inObject) throws Exception;
}
