package pro.shushi.pamirs.framework.gateways.graph.coercing;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;

public class DateScalarCoercing implements Coercing<String, Object> {

    public final static String GraphQLDate = "Date";

    @Override
    public Object serialize(Object o) {
        return o;
    }

    @Override
    public String parseValue(Object o) {
        if (null == o) {
            return null;
        }
        return (String) o;
    }

    @Override
    public String parseLiteral(Object o) {
        if (null == o) {
            return null;
        }
        if (o instanceof IntValue) {
            return ((IntValue) o).getValue().toString();
        }

        if (o instanceof StringValue) {
            String value = ((StringValue) o).getValue();
            if ("null".equalsIgnoreCase(value) || "".equalsIgnoreCase(value)) {
                return null;
            }
            return value;
        }

        return o.toString();
    }

}

