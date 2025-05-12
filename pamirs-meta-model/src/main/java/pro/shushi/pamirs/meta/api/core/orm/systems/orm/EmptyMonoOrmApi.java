package pro.shushi.pamirs.meta.api.core.orm.systems.orm;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * Orm api默认实现
 * <p>
 * 2020/7/3 11:10 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service(NamespaceConstants.spiMono)
public class EmptyMonoOrmApi extends DefaultOrmApi {

}
