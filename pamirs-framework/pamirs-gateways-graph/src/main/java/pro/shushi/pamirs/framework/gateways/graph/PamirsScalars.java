package pro.shushi.pamirs.framework.gateways.graph;

import graphql.Scalars;
import graphql.scalars.object.ObjectScalar;
import graphql.schema.GraphQLScalarType;
import pro.shushi.pamirs.framework.gateways.graph.coercing.*;

/**
 * @author drome
 * @date 2022/1/189:58 上午
 */
public class PamirsScalars extends Scalars {

    //平台重写了BigDecimal,不用gql默认的
    public static final GraphQLScalarType GraphQLBigDecimal = GraphQLScalarType.newScalar()
            .name(BigDecimalScalarCoercing.GraphQLBIGDECIMAL).description(BigDecimalScalarCoercing.GraphQLBIGDECIMAL + " custom scalar type").coercing(new BigDecimalScalarCoercing()).build();

    public static final GraphQLScalarType GraphQLDate = GraphQLScalarType.newScalar()
            .name(DateScalarCoercing.GraphQLDate).description(DateScalarCoercing.GraphQLDate + " custom scalar type").coercing(new DateScalarCoercing()).build();

    public static final GraphQLScalarType GraphQLDouble = GraphQLScalarType.newScalar()
            .name(DoubleScalarCoercing.GraphQLDOUBLE).description(DoubleScalarCoercing.GraphQLDOUBLE + " custom scalar type").coercing(new DoubleScalarCoercing()).build();

    public static final GraphQLScalarType GraphQLHtml = GraphQLScalarType.newScalar()
            .name(HtmlScalarCoercing.GraphQLHtml).description(HtmlScalarCoercing.GraphQLHtml + " custom scalar type").coercing(new HtmlScalarCoercing()).build();

    public static final GraphQLScalarType GraphQLLong = GraphQLScalarType.newScalar()
            .name(LongScalarCoercing.GraphQLLong).description(LongScalarCoercing.GraphQLLong + " custom scalar type").coercing(new LongScalarCoercing()).build();

    public static final GraphQLScalarType GraphQLMap = GraphQLScalarType.newScalar()
            .name(MapScalarCoercing.GraphQLMap).description(MapScalarCoercing.GraphQLMap + " custom scalar type").coercing(new MapScalarCoercing()).build();

    public static final GraphQLScalarType GraphQLMoney = GraphQLScalarType.newScalar()
            .name(MoneyScalarCoercing.GraphQLMoney).description(MoneyScalarCoercing.GraphQLMoney + " custom scalar type").coercing(new MoneyScalarCoercing()).build();

    public static final GraphQLScalarType GraphQLVoid = GraphQLScalarType.newScalar()
            .name(VoidScalarCoercing.GraphQLVoid).description(VoidScalarCoercing.GraphQLVoid + " custom scalar type").coercing(new VoidScalarCoercing()).build();

    public static final GraphQLScalarType GraphQLFloat = GraphQLScalarType.newScalar()
            .name(FloatScalarCoercing.GraphQLFloat).description(FloatScalarCoercing.GraphQLFloat + " custom scalar type").coercing(new FloatScalarCoercing()).build();

    public static final GraphQLScalarType GraphQLObject = new ObjectScalar();

}
