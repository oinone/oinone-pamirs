package pro.shushi.pamirs.meta.api.core.session.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.session.SessionClearService;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 默认会话清理服务
 *
 * @author Adamancy Zhang at 17:07 on 2024-04-26
 */
@Order
@Component
@SPI.Service
public class DefaultSessionClearService implements SessionClearService {
}
