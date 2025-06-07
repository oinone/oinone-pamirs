package pro.shushi.pamirs.meta.api.core.orm.systems.check;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelCheckApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 模型校验接口默认实现
 * <p>
 * 2020/7/1 8:37 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
public class DefaultModelCheckApi implements ModelCheckApi {
}
