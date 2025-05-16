package pro.shushi.pamirs.framework.gateways.graph.coercing;

import graphql.schema.Coercing;
import pro.shushi.pamirs.framework.gateways.graph.coercing.spi.MoneyScalarCoercingApi;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.math.BigDecimal;

/**
 * Money GraphQL转换处理
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2024/03/22
 */
public class MoneyScalarCoercing implements Coercing<BigDecimal, String> {

    public final static String GraphQLMoney = "Money";

    @Override
    public String serialize(Object o) {
        MoneyScalarCoercingApi moneyScalarCoercingApi = Spider.getDefaultExtension(MoneyScalarCoercingApi.class);
        return moneyScalarCoercingApi.serialize(o);
    }

    @Override
    public BigDecimal parseValue(Object o) {
        MoneyScalarCoercingApi moneyScalarCoercingApi = Spider.getDefaultExtension(MoneyScalarCoercingApi.class);
        return moneyScalarCoercingApi.parseValue(o);
    }

    @Override
    public BigDecimal parseLiteral(Object o) {
        MoneyScalarCoercingApi moneyScalarCoercingApi = Spider.getDefaultExtension(MoneyScalarCoercingApi.class);
        return moneyScalarCoercingApi.parseLiteral(o);
    }

}

