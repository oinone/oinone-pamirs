package pro.shushi.pamirs.framework.gateways.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

import java.util.ArrayList;
import java.util.List;

public class PamirsSpecBuilder {

    public RsqlQuery createSpecification(LogicalNode logicalNode, ModelConfig model) {
        List<RsqlQuery> specs = new ArrayList<>();
        RsqlQuery temp;
        for (Node node : logicalNode.getChildren()) {
            temp = createSpecification(node, model);
            if (temp != null) {
                specs.add(temp);
            }
        }

        RsqlQuery result = specs.get(0);
        if (logicalNode.getOperator() == LogicalOperator.AND) {
            for (int i = 1; i < specs.size(); i++) {
                result = result.and(specs.get(i));
            }
        } else if (logicalNode.getOperator() == LogicalOperator.OR) {
            for (int i = 1; i < specs.size(); i++) {
                result = result.or(specs.get(i));
            }
        }

        return result;
    }

    public RsqlQuery createSpecification(Node node, ModelConfig model) {
        if (node instanceof LogicalNode) {
            return createSpecification((LogicalNode) node, model);
        }
        if (node instanceof ComparisonNode) {
            return createSpecification((ComparisonNode) node, model);
        }
        return null;
    }

    public RsqlQuery createSpecification(ComparisonNode comparisonNode, ModelConfig model) {

        RsqlQuery result = new RsqlSpecification(
                comparisonNode.getSelector(),
                comparisonNode.getOperator(),
                comparisonNode.getArguments(),
                model
        ).toQuery();
        return result;
    }

}
