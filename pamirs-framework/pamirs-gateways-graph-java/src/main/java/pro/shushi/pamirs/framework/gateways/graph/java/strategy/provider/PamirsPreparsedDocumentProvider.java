package pro.shushi.pamirs.framework.gateways.graph.java.strategy.provider;

import graphql.ExecutionInput;
import graphql.ParseAndValidateResult;
import graphql.execution.instrumentation.DocumentAndVariables;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.parameters.InstrumentationCreateStateParameters;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationValidationParameters;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.language.Document;
import graphql.schema.GraphQLSchema;
import graphql.validation.ValidationError;
import pro.shushi.pamirs.framework.gateways.graph.java.strategy.parser.PamirsGQLDocumentParser;
import pro.shushi.pamirs.framework.gateways.graph.java.strategy.validation.PamirsGQLValidator;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * 重写 Document缓存、并跳过无效字段
 *
 * @author Adamancy Zhang at 16:52 on 2024-07-12
 */
@Slf4j
public class PamirsPreparsedDocumentProvider implements PreparsedDocumentProvider {

    private final GraphQLSchema graphQLSchema;

    private final Instrumentation instrumentation;

    public PamirsPreparsedDocumentProvider(GraphQLSchema graphQLSchema, Instrumentation instrumentation) {
        assert graphQLSchema != null;
        assert instrumentation != null;
        this.graphQLSchema = graphQLSchema;
        this.instrumentation = instrumentation;
    }

    @Override
    public PreparsedDocumentEntry getDocument(ExecutionInput executionInput, Function<ExecutionInput, PreparsedDocumentEntry> computeFunction) {
        AtomicReference<ExecutionInput> executionInputRef = new AtomicReference<>(executionInput);
        InstrumentationState instrumentationState = instrumentation.createState(new InstrumentationCreateStateParameters(graphQLSchema, executionInput));
        ParseAndValidateResult parseResult = parse(executionInput, graphQLSchema, instrumentationState);
        return parseAndValidate(executionInputRef, instrumentationState, parseResult);
    }

    private PreparsedDocumentEntry parseAndValidate(AtomicReference<ExecutionInput> executionInputRef, InstrumentationState instrumentationState, ParseAndValidateResult parseResult) {

        ExecutionInput executionInput = executionInputRef.get();

        if (parseResult.isFailure()) {
            log.warn("Query failed to parse : '{}'", executionInput.getQuery());
            return new PreparsedDocumentEntry(parseResult.getSyntaxException().toInvalidSyntaxError());
        } else {
            final Document document = parseResult.getDocument();
            // they may have changed the document and the variables via instrumentation so update the reference to it
            executionInput = executionInput.transform(builder -> builder.variables(parseResult.getVariables()));
            executionInputRef.set(executionInput);

            if (log.isDebugEnabled()) {
                log.debug("Validating query: '{}'", executionInput.getQuery());
            }

            final List<ValidationError> errors = validate(executionInput, document, graphQLSchema, instrumentationState);
            if (!errors.isEmpty()) {
                log.warn("Query failed to validate : '{}'", executionInput.getQuery());
                return new PreparsedDocumentEntry(errors);
            }
            return new PreparsedDocumentEntry(document);
        }
    }

    private ParseAndValidateResult parse(ExecutionInput executionInput, GraphQLSchema graphQLSchema, InstrumentationState instrumentationState) {
        InstrumentationExecutionParameters parameters = new InstrumentationExecutionParameters(executionInput, graphQLSchema, instrumentationState);
        InstrumentationContext<Document> parseInstrumentation = instrumentation.beginParse(parameters);
        CompletableFuture<Document> documentCF = new CompletableFuture<>();
        parseInstrumentation.onDispatched(documentCF);

        ParseAndValidateResult parseResult = PamirsGQLDocumentParser.getParseResult(executionInput);
        if (parseResult.isFailure()) {
            parseInstrumentation.onCompleted(null, parseResult.getSyntaxException());
            return parseResult;
        } else {
            documentCF.complete(parseResult.getDocument());
            parseInstrumentation.onCompleted(parseResult.getDocument(), null);

            DocumentAndVariables documentAndVariables = parseResult.getDocumentAndVariables();
            documentAndVariables = instrumentation.instrumentDocumentAndVariables(documentAndVariables, parameters);
            return ParseAndValidateResult.newResult()
                    .document(documentAndVariables.getDocument()).variables(documentAndVariables.getVariables()).build();
        }
    }

    private List<ValidationError> validate(ExecutionInput executionInput, Document document, GraphQLSchema graphQLSchema, InstrumentationState instrumentationState) {
        InstrumentationContext<List<ValidationError>> validationCtx = instrumentation.beginValidation(new InstrumentationValidationParameters(executionInput, document, graphQLSchema, instrumentationState));
        CompletableFuture<List<ValidationError>> cf = new CompletableFuture<>();
        validationCtx.onDispatched(cf);

        List<ValidationError> validationErrors = PamirsGQLValidator.INSTANCE.validateDocument(graphQLSchema, document);

        validationCtx.onCompleted(validationErrors, null);
        cf.complete(validationErrors);
        return validationErrors;
    }
}
