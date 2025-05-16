package pro.shushi.pamirs.meta.api.session.cache.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 获取设计器隔离OwnSign
 *
 * @author wx@shushi.pro
 */
@SPI.Service
@Component
@Order(Integer.MAX_VALUE) //默认优先级最低，业务配置需要配置成为优先级高
public class DefaultSessionFillOwnSignApi implements SessionFillOwnSignApi {
}
