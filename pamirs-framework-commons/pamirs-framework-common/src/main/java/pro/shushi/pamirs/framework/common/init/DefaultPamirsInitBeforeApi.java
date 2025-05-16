package pro.shushi.pamirs.framework.common.init;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 万物生SPI接口默认实现
 * <p>
 * 2022/5/10 6:20 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI.Service
@Order
@Component
public class DefaultPamirsInitBeforeApi implements PamirsInitBeforeApi {

    @Override
    public void preAction() {

    }

}
