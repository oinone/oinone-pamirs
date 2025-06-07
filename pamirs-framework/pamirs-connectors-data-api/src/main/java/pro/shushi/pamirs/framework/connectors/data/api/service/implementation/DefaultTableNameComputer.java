package pro.shushi.pamirs.framework.connectors.data.api.service.implementation;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.TableNameComputer;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 表名计算自定义参数提供接口默认实现
 * <p>
 * 2020/6/22 9:11 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unused")
@Order
@SPI.Service
public class DefaultTableNameComputer implements TableNameComputer {

}
