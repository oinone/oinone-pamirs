package pro.shushi.pamirs.framework.connectors.data.elastic.rsql;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlSearchOperation;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

/**
 * ElasticRSQLHelper
 *
 * @author yakir on 2022/09/08 16:27.
 */
public class ElasticRSQLHelper {

    public static BoolQuery.Builder parseRSQL(ModelConfig modelCfg, String rsql) {
        Node                              parse = new RSQLParser(RsqlSearchOperation.getOperators()).parse(rsql);
        ElasticRSQLQuery.ElasticBoolQuery query = parse.accept(new ElasticRSQLVisitor(), modelCfg);
        return query.builder();

    }
}
