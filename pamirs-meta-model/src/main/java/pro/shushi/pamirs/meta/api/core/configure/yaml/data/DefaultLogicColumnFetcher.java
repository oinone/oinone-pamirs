package pro.shushi.pamirs.meta.api.core.configure.yaml.data;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 数据配置获取接口默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@SuppressWarnings("unused")
@Order
@SPI.Service
public class DefaultLogicColumnFetcher implements LogicColumnFetcher {

}
