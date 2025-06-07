package pro.shushi.pamirs.framework.gateways.graph.java.service;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.GraphQLSdlConstants;

/**
 * DSL 根 生成器
 * <p>
 * 2020/10/20 10:33 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DslRootGenerator implements GraphQLSdlConstants {

    // 生成root
    public static void generate(StringBuilder graphQLTypeSb, StringBuilder queryBuilder, StringBuilder mutationBuilder) {
        String sb = TYPE + StringUtils.SPACE + CAPITAL_QUERY + CAPITAL_TYPE + StringUtils.SPACE + "{" + StringUtils.LF +
                queryBuilder +
                "}" + StringUtils.LF +

                TYPE + StringUtils.SPACE + CAPITAL_MUTATION + CAPITAL_TYPE + StringUtils.SPACE + "{" + StringUtils.LF +
                mutationBuilder +
                "}" + StringUtils.LF +

                SCHEMA + StringUtils.SPACE + "{" + StringUtils.LF +
                StringUtils.SPACE + QUERY + ":" + QUERY_TYPE + StringUtils.LF +
                StringUtils.SPACE + MUTATION + ":" + MUTATION_TYPE + StringUtils.LF +
                "}" + StringUtils.LF;
        graphQLTypeSb.append(sb);
    }

}
