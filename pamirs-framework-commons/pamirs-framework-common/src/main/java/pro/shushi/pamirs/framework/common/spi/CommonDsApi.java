package pro.shushi.pamirs.framework.common.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.data.DsApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 默认ds接口实现
 * <p>
 * 2020/11/10 3:43 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
@Component
public class CommonDsApi implements DsApi {
}
