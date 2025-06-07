package pro.shushi.pamirs.framework.gateways.graph.coercing.spi;

import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.math.BigDecimal;

/**
 * Float输入输出转换处理
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2024/03/22
 */
@Order
@Component
@SPI.Service
public class DefaultFloatScalarCoercing implements FloatScalarCoercingApi {

    private Double convertImpl(Object input) {
        if (isNumberIsh(input)) {
            BigDecimal value;
            try {
                value = new BigDecimal(input.toString());
            } catch (NumberFormatException e) {
                return null;
            }
            return value.doubleValue();
        } else {
            return null;
        }

    }

    @Override
    public Double serialize(Object input) {
        Double result = convertImpl(input);
        if (result == null) {
            throw new CoercingSerializeException(
                    "Expected type 'Float' but was '" + typeName(input) + "'."
            );
        }
        return result;

    }

    @Override
    public Double parseValue(Object input) {
        Double result = convertImpl(input);
        if (result == null) {
            throw new CoercingParseValueException(
                    "Expected type 'Float' but was '" + typeName(input) + "'."
            );
        }
        return result;
    }

    @Override
    public Double parseLiteral(Object input) {
        if (input instanceof IntValue) {
            return ((IntValue) input).getValue().doubleValue();
        } else if (input instanceof FloatValue) {
            return ((FloatValue) input).getValue().doubleValue();
        } else {
            throw new CoercingParseLiteralException(
                    "Expected AST type 'IntValue' or 'FloatValue' but was '" + typeName(input) + "'."
            );
        }
    }

    private static boolean isNumberIsh(Object input) {
        return input instanceof Number || input instanceof String;
    }

    private static String typeName(Object input) {
        if (input == null) {
            return "null";
        }

        return input.getClass().getSimpleName();
    }

}
