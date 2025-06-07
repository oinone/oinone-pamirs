package pro.shushi.pamirs.middleware.schedule.core.serialize.extension;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.middleware.schedule.core.serialize.KryoRegisterApi;

/**
 * KRYO register API实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
public class DefaultKryoRegisterApi implements KryoRegisterApi {

}
