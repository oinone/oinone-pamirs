package pro.shushi.pamirs.meta.api.core.configure.yaml.data;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 动态数据源路由参数接口默认实现
 * <p>
 * 2020/7/8 8:41 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unused")
@Order
@SPI.Service
public class DefaultDynamicDsKeyComputer implements DynamicDsKeyComputer {
}
