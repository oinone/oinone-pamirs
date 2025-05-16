package pro.shushi.pamirs.eip.api;

import org.apache.camel.ExtendedExchange;

public interface IEipScenetDefinition<T> {

    default Object sourcePreConvert(ExtendedExchange exchange, Object inObject) {
        return inObject;
    }

    @Deprecated
    default Boolean sourceCoreConvert(IEipContext<T> context, ExtendedExchange exchange) {
        return Boolean.TRUE;
    }

    default Object sourceAfterConvert(ExtendedExchange exchange, Object inObject) {
        return inObject;
    }

    default Object targetPreConvert(ExtendedExchange exchange, Object inObject) {
        return inObject;
    }

    @Deprecated
    default Boolean targetCoreConvert(IEipContext<T> context, ExtendedExchange exchange) {
        return Boolean.TRUE;
    }

    default Object targetAfterConvert(ExtendedExchange exchange, Object inObject) {
        return inObject;
    }
}
