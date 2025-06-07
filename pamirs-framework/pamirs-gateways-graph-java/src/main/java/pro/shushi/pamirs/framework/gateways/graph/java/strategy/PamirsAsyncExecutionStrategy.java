package pro.shushi.pamirs.framework.gateways.graph.java.strategy;

import graphql.AssertException;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.execution.*;
import graphql.introspection.Introspection;
import graphql.language.Field;
import graphql.schema.*;
import pro.shushi.pamirs.framework.common.entry.NullValue;
import pro.shushi.pamirs.framework.gateways.graph.enmu.GqlExpEnumerate;
import pro.shushi.pamirs.framework.gateways.graph.java.strategy.type.GraphQLEnumTypeProxy;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.concurrent.CompletableFuture;

import static graphql.execution.FieldValueInfo.CompleteValueType.LIST;
import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Pamirs GQL Async Execution Strategy
 *
 * @author Adamancy Zhang at 01:02 on 2024-05-04
 */
public class PamirsAsyncExecutionStrategy extends AsyncExecutionStrategy {

    public PamirsAsyncExecutionStrategy() {
        super();
    }

    public PamirsAsyncExecutionStrategy(DataFetcherExceptionHandler exceptionHandler) {
        super(exceptionHandler);
    }

    @Override
    protected FieldValueInfo completeValueForList(ExecutionContext executionContext, ExecutionStrategyParameters parameters, Object result) {
        if (NullValue.INSTANCE.equals(result)) {
            return FieldValueInfo.newFieldValueInfo(LIST).fieldValue(completedFuture(new ExecutionResultImpl(result, null))).build();
        }
        GraphQLList list = (GraphQLList) parameters.getExecutionStepInfo().getUnwrappedNonNullType();
        if (list.getWrappedType() instanceof GraphQLEnumType && result instanceof Number) {
            return FieldValueInfo.newFieldValueInfo(LIST).fieldValue(completedFuture(new ExecutionResultImpl(result, null))).build();
        }
        return super.completeValueForList(executionContext, parameters, result);
    }

    @Override
    protected CompletableFuture<ExecutionResult> completeValueForScalar(ExecutionContext executionContext, ExecutionStrategyParameters parameters, GraphQLScalarType scalarType, Object result) {
        if (NullValue.INSTANCE.equals(result)) {
            return completedFuture(new ExecutionResultImpl(result, null));
        }
        return super.completeValueForScalar(executionContext, parameters, scalarType, result);
    }

    @Override
    protected CompletableFuture<ExecutionResult> completeValueForEnum(ExecutionContext executionContext, ExecutionStrategyParameters parameters, GraphQLEnumType enumType, Object result) {
        if (NullValue.INSTANCE.equals(result)) {
            return completedFuture(new ExecutionResultImpl(result, null));
        }
        return super.completeValueForEnum(executionContext, parameters, new GraphQLEnumTypeProxy(enumType), result);
    }

    @Override
    protected CompletableFuture<ExecutionResult> completeValueForObject(ExecutionContext executionContext, ExecutionStrategyParameters parameters, GraphQLObjectType resolvedObjectType, Object result) {
        if (NullValue.INSTANCE.equals(result)) {
            // 假装执行一层，避免GQL内部层级判断错误
            ExecutionStepInfo executionStepInfo = parameters.getExecutionStepInfo();
            MergedSelectionSet subFields = MergedSelectionSet.newMergedSelectionSet().build();
            ExecutionStepInfo newExecutionStepInfo = executionStepInfo.changeTypeWithPreservedNonNull(resolvedObjectType);
            ExecutionStrategyParameters newParameters = parameters.transform(builder ->
                    builder.executionStepInfo(newExecutionStepInfo)
                            .fields(subFields)
                            .source(null)
            );
            executionContext.getQueryStrategy().execute(executionContext, newParameters);
            return completedFuture(new ExecutionResultImpl(result, null));
        }
        return super.completeValueForObject(executionContext, parameters, resolvedObjectType, result);
    }

    @Override
    protected GraphQLFieldDefinition getFieldDef(GraphQLSchema schema, GraphQLObjectType parentType, Field field) {
        return getFieldDef(schema, parentType, field.getName());
    }

    /**
     * @see Introspection#getFieldDef(graphql.schema.GraphQLSchema, graphql.schema.GraphQLCompositeType, java.lang.String)
     */
    protected GraphQLFieldDefinition getFieldDef(GraphQLSchema schema, GraphQLCompositeType parentType, String fieldName) {
        if (schema.getQueryType() == parentType) {
            if (fieldName.equals(Introspection.SchemaMetaFieldDef.getName())) {
                return Introspection.SchemaMetaFieldDef;
            }
            if (fieldName.equals(Introspection.TypeMetaFieldDef.getName())) {
                return Introspection.TypeMetaFieldDef;
            }
        }
        if (fieldName.equals(Introspection.TypeNameMetaFieldDef.getName())) {
            return Introspection.TypeNameMetaFieldDef;
        }

        if (!(parentType instanceof GraphQLFieldsContainer)) {
            throw new AssertException(String.format("should not happen : parent type must be an object or interface %s", parentType));
        }
        GraphQLFieldsContainer fieldsContainer = (GraphQLFieldsContainer) parentType;
        GraphQLFieldDefinition fieldDefinition = schema.getCodeRegistry().getFieldVisibility().getFieldDefinition(fieldsContainer, fieldName);
        if (fieldDefinition == null) {
            //如果前端请求中有删除掉的字段，这里提示。
            String esg = String.format("Unknown field '%s'", fieldName);
            throw PamirsException.construct(GqlExpEnumerate.BASE_GRAPHQL_FIELD_UNDEFINED_ERROR).appendMsg(esg).errThrow();
        }
        return fieldDefinition;
    }
}
