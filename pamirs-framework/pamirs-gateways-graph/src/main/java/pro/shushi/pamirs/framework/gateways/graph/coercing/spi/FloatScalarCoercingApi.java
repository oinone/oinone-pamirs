package pro.shushi.pamirs.framework.gateways.graph.coercing.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * Float输入输出转换处理API
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2024/03/22
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface FloatScalarCoercingApi {

    Double serialize(Object input);

    Double parseValue(Object input);

    Double parseLiteral(Object input);

}
