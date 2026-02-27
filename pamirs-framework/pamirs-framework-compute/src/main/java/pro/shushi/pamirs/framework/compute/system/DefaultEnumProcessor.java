package pro.shushi.pamirs.framework.compute.system;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.compute.systems.enmu.EnumProcessor;
import pro.shushi.pamirs.meta.api.core.orm.systems.enums.BaseEnumProcessor;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;

/**
 * 枚举处理器默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@Order(0)
@Component
@SPI.Service
public class DefaultEnumProcessor extends BaseEnumProcessor implements EnumProcessor<DataDictionary> {

}
