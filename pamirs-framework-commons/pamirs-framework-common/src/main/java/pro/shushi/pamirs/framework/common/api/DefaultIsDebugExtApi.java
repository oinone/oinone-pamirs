package pro.shushi.pamirs.framework.common.api;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 是否为debug，业务自行扩展API
 * <p>
 * 2024/4/3 5:53 下午
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
public class DefaultIsDebugExtApi implements IsDebugExtApi {

    @Override
    public boolean isDebug() {
        return true;
    }
}
