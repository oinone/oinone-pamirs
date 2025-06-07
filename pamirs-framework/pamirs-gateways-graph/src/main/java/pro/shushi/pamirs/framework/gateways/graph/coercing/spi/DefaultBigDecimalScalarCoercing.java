package pro.shushi.pamirs.framework.gateways.graph.coercing.spi;

import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.math.BigDecimal;

/**
 * BigDecimal输入输出转换处理
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2024/03/22
 */
@Order
@Component
@SPI.Service
public class DefaultBigDecimalScalarCoercing implements BigDecimalScalarCoercingApi {

    @Override
    public String serialize(Object o) {
        if (null == o) {
            return null;
        }
        String value = String.valueOf(o);
        if ("null".equalsIgnoreCase(value) || "".equalsIgnoreCase(value)) {
            return null;
        }
        if (o instanceof BigDecimal) {
            return ((BigDecimal) o).toPlainString();
        }
        return value;
    }

    @Override
    public BigDecimal parseValue(Object o) {
        String value = String.valueOf(o);
        if ("null".equalsIgnoreCase(value) || "".equals(value)) {
            return null;
        }
        return new BigDecimal(value);
    }

    @Override
    public BigDecimal parseLiteral(Object o) {
        if (null == o) {
            return null;
        }
        BigDecimal value = null;
        if (o instanceof IntValue) {
            value = new BigDecimal(((IntValue) o).getValue());
        } else if (o instanceof FloatValue) {
            value = ((FloatValue) o).getValue();
        } else if (o instanceof StringValue) {
            value = new BigDecimal(((StringValue) o).getValue());
        }
        return value;
    }
}
