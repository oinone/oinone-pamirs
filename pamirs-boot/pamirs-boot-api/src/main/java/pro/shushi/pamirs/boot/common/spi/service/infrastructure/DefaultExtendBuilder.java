package pro.shushi.pamirs.boot.common.spi.service.infrastructure;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.spi.api.infrastructure.ExtendBuilderApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 启动扩展构建实现
 * <p>
 * 2020/8/27 5:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(99)
@Component
@SPI.Service
public class DefaultExtendBuilder implements ExtendBuilderApi {

}
