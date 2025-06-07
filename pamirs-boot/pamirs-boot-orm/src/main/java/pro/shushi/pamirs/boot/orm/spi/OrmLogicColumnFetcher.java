package pro.shushi.pamirs.boot.orm.spi;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.LogicColumnFetcher;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 逻辑字段获取
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@SuppressWarnings("unused")
@Order(88)
@SPI.Service
public class OrmLogicColumnFetcher implements LogicColumnFetcher {

}
