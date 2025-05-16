package pro.shushi.pamirs.framework.gateways.graph.coercing.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.math.BigDecimal;

/**
 * Money输入输出转换处理API
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2024/03/22
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface MoneyScalarCoercingApi {

    String serialize(Object o);

    BigDecimal parseValue(Object o);

    BigDecimal parseLiteral(Object o);


}

