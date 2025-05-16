package pro.shushi.pamirs.framework.gateways.graph.coercing;

import graphql.language.FloatValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;

public class DoubleScalarCoercing implements Coercing<Double, String> {

    public final static String GraphQLDOUBLE = "DOUBLE";

    // output
    @Override
    public String serialize(Object o) {
        if (null == o) {
            return null;
        }
        String value = String.valueOf(o);
        if ("null".equalsIgnoreCase(value) || "".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

    // input
    @Override
    public Double parseValue(Object o) {
        String value = String.valueOf(o);
        if ("null".equalsIgnoreCase(value) || "".equals(value)) {
            return null;
        }
        return Double.parseDouble(value);
    }

    // input
    @Override
    public Double parseLiteral(Object o) {
        if (null == o) {
            return null;
        }

        Double value = null;
        if (o instanceof FloatValue) {
            value = ((FloatValue) o).getValue().doubleValue();
        } else if (o instanceof StringValue) {
            value = Double.parseDouble(((StringValue) o).getValue());
        }

        return value;
    }

}

