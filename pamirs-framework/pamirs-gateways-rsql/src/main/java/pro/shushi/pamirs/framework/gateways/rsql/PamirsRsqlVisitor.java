package pro.shushi.pamirs.framework.gateways.rsql;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

public class PamirsRsqlVisitor implements RSQLVisitor<RsqlQuery, ModelConfig> {

    private PamirsSpecBuilder builder;

    public PamirsRsqlVisitor() {
        this.builder = new PamirsSpecBuilder();
    }

    @Override
    public RsqlQuery visit(AndNode andNode, ModelConfig model) {
        return builder.createSpecification(andNode, model);
    }

    @Override
    public RsqlQuery visit(OrNode orNode, ModelConfig model) {
        return builder.createSpecification(orNode, model);
    }

    @Override
    public RsqlQuery visit(ComparisonNode comparisonNode, ModelConfig model) {
        return builder.createSpecification(comparisonNode, model);
    }
}
