package pro.shushi.pamirs.boot.common.spi.service.session;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.spi.SessionPrepareApi;
import pro.shushi.pamirs.framework.common.spi.service.SessionPrepareTemplate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 默认session处理实现
 *
 * @author Adamancy Zhang on 2021-04-20 17:59
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultSessionPrepare extends SessionPrepareTemplate implements SessionPrepareApi {
}
