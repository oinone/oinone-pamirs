package pro.shushi.pamirs.framework.gateways.graph.coercing;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;

public class LongScalarCoercing implements Coercing<Long, String> {

    public final static String GraphQLLong = "Long";

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
    public Long parseValue(Object o) {
        String value = String.valueOf(o);
        if ("null".equalsIgnoreCase(value) || "".equals(value)) {
            return null;
        }
        return Long.parseLong(value);
    }

    // input
    @Override
    public Long parseLiteral(Object o) {
        if (null == o) {
            return null;
        }

        Long value = null;
        if (o instanceof IntValue) {
            value = ((IntValue) o).getValue().longValue();
        } else if (o instanceof StringValue) {
            String s = ((StringValue) o).getValue();
            if ("null".equalsIgnoreCase(s) || "".equals(s)) {
                return null;
            }
            value = Long.parseLong(s);
        }

        return value;
    }

}

