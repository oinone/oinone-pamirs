package pro.shushi.pamirs.framework.gateways.graph.coercing;

import graphql.schema.Coercing;
import pro.shushi.pamirs.framework.gateways.graph.coercing.spi.FloatScalarCoercingApi;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * Float GraphQL转换处理
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2024/03/22
 */
public class FloatScalarCoercing implements Coercing<Double, Double> {

    public final static String GraphQLFloat = "Float";

    @Override
    public Double serialize(Object o) {
        FloatScalarCoercingApi floatScalarCoercingApi = Spider.getDefaultExtension(FloatScalarCoercingApi.class);
        return floatScalarCoercingApi.serialize(o);
    }

    @Override
    public Double parseValue(Object o) {
        FloatScalarCoercingApi floatScalarCoercingApi = Spider.getDefaultExtension(FloatScalarCoercingApi.class);
        return floatScalarCoercingApi.parseValue(o);
    }

    @Override
    public Double parseLiteral(Object o) {
        FloatScalarCoercingApi floatScalarCoercingApi = Spider.getDefaultExtension(FloatScalarCoercingApi.class);
        return floatScalarCoercingApi.parseLiteral(o);
    }

}
