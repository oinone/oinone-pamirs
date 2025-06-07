package pro.shushi.pamirs.framework.gateways.graph.java.strategy.type;

import graphql.language.EnumTypeDefinition;
import graphql.language.EnumTypeExtensionDefinition;
import graphql.schema.*;
import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * GraphQLEnumTypeProxy
 *
 * @author Adamancy Zhang at 09:34 on 2024-07-12
 */
public class GraphQLEnumTypeProxy extends GraphQLEnumType {

    private final GraphQLEnumType origin;

    public GraphQLEnumTypeProxy(GraphQLEnumType origin) {
        super(CharacterConstants.SEPARATOR_UNDERLINE, CharacterConstants.SEPARATOR_EMPTY, Collections.emptyList(), Collections.emptyList(), null);
        this.origin = origin;
    }

    @Override
    public Object serialize(Object input) {
        return input;
    }

    @Override
    public Object parseValue(Object input) {
        return origin.parseValue(input);
    }

    @Override
    public Object parseLiteral(Object input) {
        return origin.parseLiteral(input);
    }

    @Override
    public List<GraphQLEnumValueDefinition> getValues() {
        return origin.getValues();
    }

    @Override
    public GraphQLEnumValueDefinition getValue(String name) {
        return origin.getValue(name);
    }

    @Override
    public String getName() {
        return origin.getName();
    }

    @Override
    public String getDescription() {
        return origin.getDescription();
    }

    @Override
    public EnumTypeDefinition getDefinition() {
        return origin.getDefinition();
    }

    @Override
    public List<EnumTypeExtensionDefinition> getExtensionDefinitions() {
        return origin.getExtensionDefinitions();
    }

    @Override
    public List<GraphQLDirective> getDirectives() {
        return origin.getDirectives();
    }

    @Override
    public GraphQLEnumType transform(Consumer<Builder> builderConsumer) {
        return origin.transform(builderConsumer);
    }

    @Override
    public TraversalControl accept(TraverserContext<GraphQLSchemaElement> context, GraphQLTypeVisitor visitor) {
        return origin.accept(context, visitor);
    }

    @Override
    public List<GraphQLSchemaElement> getChildren() {
        return origin.getChildren();
    }

    @Override
    public SchemaElementChildrenContainer getChildrenWithTypeReferences() {
        return origin.getChildrenWithTypeReferences();
    }

    @Override
    public GraphQLEnumType withNewChildren(SchemaElementChildrenContainer newChildren) {
        return origin.withNewChildren(newChildren);
    }

    @Override
    public Map<String, GraphQLDirective> getDirectivesByName() {
        return origin.getDirectivesByName();
    }

    @Override
    public GraphQLDirective getDirective(String directiveName) {
        return origin.getDirective(directiveName);
    }
}
