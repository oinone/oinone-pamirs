package pro.shushi.pamirs.framework.gateways.graph.java.strategy.validation;

import graphql.language.Field;
import graphql.schema.GraphQLCompositeType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.validation.AbstractRule;
import graphql.validation.ValidationContext;
import graphql.validation.ValidationErrorCollector;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

/**
 * 重写GQL字段校验逻辑
 *
 * @author Adamancy Zhang at 17:43 on 2024-07-20
 */
@Slf4j
public class PamirsFieldsOnCorrectType extends AbstractRule {

    public PamirsFieldsOnCorrectType(ValidationContext validationContext, ValidationErrorCollector validationErrorCollector) {
        super(validationContext, validationErrorCollector);
    }


    @Override
    public void checkField(Field field) {
        ValidationContext validationContext = getValidationContext();
        GraphQLCompositeType parentType = validationContext.getParentType();
        // this means the parent type is not a CompositeType, which is an error handled elsewhere
        if (parentType == null) {
            return;
        }
        GraphQLFieldDefinition fieldDef = validationContext.getFieldDef();
        if (fieldDef == null) {
            if (log.isWarnEnabled()) {
                log.warn("Field '{}' in type '{}' is undefined", field.getName(), parentType.getName());
            }
        }
    }
}
