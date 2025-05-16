package pro.shushi.pamirs.meta.api.session.cache.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 默认重置元数据缓存API
 *
 * @author Adamancy Zhang at 12:37 on 2024-10-26
 */
@Order
@Component
@SPI.Service
public class DefaultResetSessionCacheApi implements ResetSessionCacheApi {
}
