package pro.shushi.pamirs.eip.api;

import org.apache.camel.ExtendedExchange;

/**
 * 解密处理器
 *
 * @author yeshenyue on 2025/4/25 10:42.
 */
@FunctionalInterface
public interface IEipDecryptProcessor {

    void processor(ExtendedExchange exchange);
}
