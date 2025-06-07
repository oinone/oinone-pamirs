package pro.shushi.pamirs.framework.gateways.graph.coercing;

import graphql.schema.Coercing;
import pro.shushi.pamirs.framework.gateways.graph.coercing.spi.BigDecimalScalarCoercingApi;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.math.BigDecimal;

/**
 * BigDecimal GraphQL转换处理
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2024/03/22
 */
public class BigDecimalScalarCoercing implements Coercing<BigDecimal, String> {

    public final static String GraphQLBIGDECIMAL = "BigDecimal";

    // output
    @Override
    public String serialize(Object o) {
        BigDecimalScalarCoercingApi bigDecimalScalarCoercingApi = Spider.getDefaultExtension(BigDecimalScalarCoercingApi.class);
        return bigDecimalScalarCoercingApi.serialize(o);
    }

    // input
    @Override
    public BigDecimal parseValue(Object o) {
        BigDecimalScalarCoercingApi bigDecimalScalarCoercingApi = Spider.getDefaultExtension(BigDecimalScalarCoercingApi.class);
        return bigDecimalScalarCoercingApi.parseValue(o);
    }

    // input
    @Override
    public BigDecimal parseLiteral(Object o) {
        BigDecimalScalarCoercingApi bigDecimalScalarCoercingApi = Spider.getDefaultExtension(BigDecimalScalarCoercingApi.class);
        return bigDecimalScalarCoercingApi.parseLiteral(o);
    }

}

