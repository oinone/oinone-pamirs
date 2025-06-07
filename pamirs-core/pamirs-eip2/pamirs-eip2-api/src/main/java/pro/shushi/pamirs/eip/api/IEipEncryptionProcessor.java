package pro.shushi.pamirs.eip.api;

import org.apache.camel.ExtendedExchange;

/**
 * 加密处理器
 *
 * @author yeshenyue on 2025/4/25 09:01.
 */
@FunctionalInterface
public interface IEipEncryptionProcessor {

    Object processor(ExtendedExchange exchange, Object body);
}
