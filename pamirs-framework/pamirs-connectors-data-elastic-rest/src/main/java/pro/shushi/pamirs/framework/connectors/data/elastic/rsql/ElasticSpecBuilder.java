package pro.shushi.pamirs.framework.connectors.data.elastic.rsql;

import com.google.common.collect.Lists;
import cz.jirutka.rsql.parser.ast.*;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlExtendOperator;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

import java.util.List;

import static pro.shushi.pamirs.framework.connectors.data.elastic.rsql.ElasticRSQLQuery.ElasticBoolQuery;

/**
 * ElasticSpecBuilder
 *
 * @author yakir on 2022/09/07 18:11.
 */
public class ElasticSpecBuilder {

    public ElasticBoolQuery createSpecification(ElasticBoolQuery root, LogicalNode logicalNode, ModelConfig model) {

        LogicalOperator operator = logicalNode.getOperator();

        List<Node> children = logicalNode.getChildren();

        for (Node node : children) {
            createSpecification(root, node, model, operator);
        }

        return root;
    }

    public ElasticBoolQuery createSpecification(ElasticBoolQuery root, Node node, ModelConfig model, LogicalOperator operator) {
        if (node instanceof LogicalNode) {
            ElasticBoolQuery boolQuery = createSpecification(ElasticBoolQuery.bool(), (LogicalNode) node, model);
            if (operator == LogicalOperator.AND) {
                root.and(boolQuery);
            } else if (operator == LogicalOperator.OR) {
                root.or(boolQuery);
            } else {
                root.and(boolQuery);
            }
            return root;
        } else if (node instanceof ComparisonNode) {
            return createSpecification(root, (ComparisonNode) node, model, operator);
        }

        return root;
    }

    public ElasticBoolQuery createSpecification(ElasticBoolQuery root, ComparisonNode comparisonNode, ModelConfig model, LogicalOperator operator) {
        if (StringUtils.equals("==", comparisonNode.getOperator().getSymbol())
                && StringUtils.equals("1", comparisonNode.getSelector())
                && comparisonNode.getArguments().size() == 1
                && "1".equals(comparisonNode.getArguments().get(0))) {

            comparisonNode = new ComparisonNode(RSQLOperators.GREATER_THAN, "id", Lists.newArrayList("0"));
        }

        ComparisonOperator comparisonOperator = comparisonNode.getOperator();
        List<String> comparisonArgs = comparisonNode.getArguments();
        ElasticRSQLQuery rsqlQuery = new ElasticRSQLSpec(comparisonNode.getSelector(), comparisonOperator, comparisonArgs, model)
                .toQuery();

        if (RsqlExtendOperator.ISNULL.equals(comparisonOperator)) {
            root.not(rsqlQuery);
        } else if (operator == LogicalOperator.AND) {
            root.and(rsqlQuery);
        } else if (operator == LogicalOperator.OR) {
            root.or(rsqlQuery);
        } else {
            root.and(rsqlQuery);
        }

        return root;
    }
}
