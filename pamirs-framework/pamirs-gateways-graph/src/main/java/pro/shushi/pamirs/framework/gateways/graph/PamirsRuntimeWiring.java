package pro.shushi.pamirs.framework.gateways.graph;

import graphql.Scalars;
import graphql.language.ScalarTypeDefinition;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.ScalarInfo;

public class PamirsRuntimeWiring {

//    内置的基础类型,加入到集合中. 构造schema时,不会被复制 graphql.schema.idl.ScalarInfo.isGraphqlSpecifiedScalar(graphql.schema.GraphQLScalarType)
//    如果基础类型被复制, 启动没问题, 但是运行时增加模型重新构造schema时, 目前未从之前的schema中获取基础类型,导致相同name的type内存地址不一致,校验不通过
    static {
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS.add(PamirsScalars.GraphQLBigDecimal);
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS.add(PamirsScalars.GraphQLDate);
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS.add(PamirsScalars.GraphQLDouble);
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS.add(PamirsScalars.GraphQLHtml);
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS.add(PamirsScalars.GraphQLLong);
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS.add(PamirsScalars.GraphQLMap);
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS.add(PamirsScalars.GraphQLMoney);
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS.add(PamirsScalars.GraphQLVoid);
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS.add(PamirsScalars.GraphQLObject);

        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS.add(Scalars.GraphQLByte);
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS.add(Scalars.GraphQLShort);
    }

    static {
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS_DEFINITIONS.put(PamirsScalars.GraphQLBigDecimal.getName(), ScalarTypeDefinition.newScalarTypeDefinition().name(PamirsScalars.GraphQLBigDecimal.getName()).build());
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS_DEFINITIONS.put(PamirsScalars.GraphQLDate.getName(), ScalarTypeDefinition.newScalarTypeDefinition().name(PamirsScalars.GraphQLDate.getName()).build());
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS_DEFINITIONS.put(PamirsScalars.GraphQLDouble.getName(), ScalarTypeDefinition.newScalarTypeDefinition().name(PamirsScalars.GraphQLDouble.getName()).build());
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS_DEFINITIONS.put(PamirsScalars.GraphQLHtml.getName(), ScalarTypeDefinition.newScalarTypeDefinition().name(PamirsScalars.GraphQLHtml.getName()).build());
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS_DEFINITIONS.put(PamirsScalars.GraphQLLong.getName(), ScalarTypeDefinition.newScalarTypeDefinition().name(PamirsScalars.GraphQLLong.getName()).build());
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS_DEFINITIONS.put(PamirsScalars.GraphQLMap.getName(), ScalarTypeDefinition.newScalarTypeDefinition().name(PamirsScalars.GraphQLMap.getName()).build());
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS_DEFINITIONS.put(PamirsScalars.GraphQLMoney.getName(), ScalarTypeDefinition.newScalarTypeDefinition().name(PamirsScalars.GraphQLMoney.getName()).build());
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS_DEFINITIONS.put(PamirsScalars.GraphQLVoid.getName(), ScalarTypeDefinition.newScalarTypeDefinition().name(PamirsScalars.GraphQLVoid.getName()).build());
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS_DEFINITIONS.put(PamirsScalars.GraphQLObject.getName(), ScalarTypeDefinition.newScalarTypeDefinition().name(PamirsScalars.GraphQLObject.getName()).build());

        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS_DEFINITIONS.put(Scalars.GraphQLByte.getName(), ScalarTypeDefinition.newScalarTypeDefinition().name(Scalars.GraphQLByte.getName()).build());
        ScalarInfo.GRAPHQL_SPECIFICATION_SCALARS_DEFINITIONS.put(Scalars.GraphQLShort.getName(), ScalarTypeDefinition.newScalarTypeDefinition().name(Scalars.GraphQLShort.getName()).build());
    }

    public static RuntimeWiring.Builder newRuntimeWiring(){
        // 保证静态代码执行顺序
        return RuntimeWiring.newRuntimeWiring();
    }
}
