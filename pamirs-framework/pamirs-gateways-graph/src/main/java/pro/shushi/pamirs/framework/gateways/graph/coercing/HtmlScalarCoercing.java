package pro.shushi.pamirs.framework.gateways.graph.coercing;

import graphql.language.StringValue;
import graphql.schema.Coercing;

public class HtmlScalarCoercing implements Coercing<String, String> {

    public final static String GraphQLHtml = "Html";

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

    @Override
    public String parseValue(Object o) {
        if (null == o) {
            return null;
        }
        return o.toString();
    }

    @Override
    public String parseLiteral(Object o) {
        if (null == o) {
            return null;
        }
        String value = ((StringValue) o).getValue();
        if ("null".equalsIgnoreCase(value) || "".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

}

