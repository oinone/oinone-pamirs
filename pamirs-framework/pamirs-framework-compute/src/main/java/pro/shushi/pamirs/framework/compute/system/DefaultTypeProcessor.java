package pro.shushi.pamirs.framework.compute.system;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.core.orm.systems.types.BaseTypeProcessor;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 类型处理器默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@SuppressWarnings("unused")
@Slf4j
@SPI.Service(NamespaceConstants.spiDefault)
@Component
public class DefaultTypeProcessor extends BaseTypeProcessor implements TypeProcessor {

}
