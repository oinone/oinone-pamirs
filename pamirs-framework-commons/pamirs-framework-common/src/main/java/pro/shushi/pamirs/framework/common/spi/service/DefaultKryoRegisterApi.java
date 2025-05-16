package pro.shushi.pamirs.framework.common.spi.service;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.framework.common.spi.KryoRegisterApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

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
