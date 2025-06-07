package pro.shushi.pamirs.framework.connectors.data.elastic.rsql;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

import static pro.shushi.pamirs.framework.connectors.data.elastic.rsql.ElasticRSQLQuery.ElasticBoolQuery;

/**
 * ElasticRSQLVisitor
 *
 * @author yakir on 2022/09/07 18:09.
 */
public class ElasticRSQLVisitor implements RSQLVisitor<ElasticBoolQuery, ModelConfig> {

    private ElasticSpecBuilder elasticSpecBuilder;

    public ElasticRSQLVisitor() {
        this.elasticSpecBuilder = new ElasticSpecBuilder();
    }

    @Override
    public ElasticBoolQuery visit(AndNode andNode, ModelConfig model) {
        return elasticSpecBuilder.createSpecification(ElasticBoolQuery.bool(), andNode, model);
    }

    @Override
    public ElasticBoolQuery visit(OrNode orNode, ModelConfig model) {
        return elasticSpecBuilder.createSpecification(ElasticBoolQuery.bool(), orNode, model);
    }

    @Override
    public ElasticBoolQuery visit(ComparisonNode comparisonNode, ModelConfig model) {
        return elasticSpecBuilder.createSpecification(ElasticBoolQuery.bool(), comparisonNode, model, null);
    }
}
