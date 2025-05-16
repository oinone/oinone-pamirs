package pro.shushi.pamirs.framework.gateways.graph.coercing;

import graphql.schema.Coercing;

public class VoidScalarCoercing implements Coercing<Object, Object> {

    public final static String GraphQLVoid = "Void";

    @Override
    public Object serialize(Object o) {
        return o;
    }

    @Override
    public Object parseValue(Object o) {
        return o;
    }

    @Override
    public Object parseLiteral(Object o) {
        return o;
    }

}

