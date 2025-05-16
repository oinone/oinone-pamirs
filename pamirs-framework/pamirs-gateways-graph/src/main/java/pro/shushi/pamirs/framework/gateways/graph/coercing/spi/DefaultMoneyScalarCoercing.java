package pro.shushi.pamirs.framework.gateways.graph.coercing.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * Money输入输出转换处理
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2024/03/22
 */
@Order
@Component
@SPI.Service
public class DefaultMoneyScalarCoercing extends DefaultBigDecimalScalarCoercing implements MoneyScalarCoercingApi {
}
