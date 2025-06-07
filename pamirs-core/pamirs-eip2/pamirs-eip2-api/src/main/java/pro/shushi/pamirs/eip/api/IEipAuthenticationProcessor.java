package pro.shushi.pamirs.eip.api;

import org.apache.camel.ExtendedExchange;

/**
 * Eip认证处理器
 *
 * @param <T> 上下文承载类型
 * @author Adamancy Zhang at 10:38 on 2021-05-09
 */
@FunctionalInterface
public interface IEipAuthenticationProcessor<T> {

    /**
     * <h>认证</h>
     *
     * @param context  上下文
     * @param exchange 交换对象
     * @return 认证是否成功
     */
    boolean authentication(IEipContext<T> context, ExtendedExchange exchange);

    /**
     * <h>验签</h>
     *
     * @param context  上下文
     * @param exchange 交换对象
     */
    default void signature(IEipContext<T> context, ExtendedExchange exchange) {
    }
}
