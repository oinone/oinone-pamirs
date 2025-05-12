package pro.shushi.pamirs.meta.api.core.orm.systems.inherited;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelInheritedApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 模型继承系统默认实现
 * <p>
 * 2020/7/1 8:37 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
public class DefaultModelInheritedApi implements ModelInheritedApi {
}
