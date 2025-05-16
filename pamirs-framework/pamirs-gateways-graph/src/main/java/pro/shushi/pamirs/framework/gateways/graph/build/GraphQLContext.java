package pro.shushi.pamirs.framework.gateways.graph.build;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import pro.shushi.pamirs.framework.gateways.graph.PamirsRuntimeWiring;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * GraphQL构造器上下文
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:31 上午
 */
@Data
public class GraphQLContext {

    private GraphQLSchema.Builder graphQlSchema;

    private RuntimeWiring.Builder runtimeWiring = PamirsRuntimeWiring.newRuntimeWiring();
//            .scalar(new DateScalarType())
//            .scalar(new LongScalarType())
//            .scalar(new BigDecimalScalarType())
//            .scalar(new MoneyScalarType())
//            .scalar(new HtmlScalarType());

    private StringBuilder graphqlRootSb = new StringBuilder();

    private StringBuilder graphqlSchemaSb = new StringBuilder();

    // query & mutation define content
    private StringBuilder graphqlQuerySb = new StringBuilder();
    private StringBuilder graphqlMutationSb = new StringBuilder();

    public StringBuilder appandGraphqlRoot(StringBuilder sb) {
        return graphqlRootSb.append(sb);
    }

    public StringBuilder appandGraphqlSchema(StringBuilder sb) {
        return graphqlSchemaSb.append(sb);
    }

    public StringBuilder appandGraphqlQuery(StringBuilder sb) {
        return graphqlQuerySb.append(sb);
    }

    public StringBuilder appandGraphqlMutation(StringBuilder sb) {
        return graphqlMutationSb.append(sb);
    }

}
