package pro.shushi.pamirs.framework.gateways.graph.instrument;

import graphql.*;
import graphql.execution.ExecutionPath;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.gateways.graph.containts.ResultExtensionsConstants;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.util.GraphQLUtils;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.msg.ErrorExtension;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static graphql.execution.instrumentation.SimpleInstrumentationContext.whenCompleted;

public class MessageHubInstrumentation extends SimpleInstrumentation {

    private final static Logger logger = LoggerFactory.getLogger(MessageHubInstrumentation.class);

    @Override
    public InstrumentationState createState() {
        //
        // instrumentation state is passed during each invocation of an Instrumentation method
        // and allows you to put stateful data away and reference it during the query execution
        //
        return new MessageHubInstrumentationState();
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
        PamirsSession.getMessageHub().clear();
        return new SimpleInstrumentationContext<ExecutionResult>() {
            @Override
            public void onCompleted(ExecutionResult result, Throwable t) {
                fillInternalMessageHub(parameters.getInstrumentationState(), t);
                PamirsSession.getMessageHub().clear();
            }
        };
    }

    @Override
    public InstrumentationContext<Object> beginFieldFetch(InstrumentationFieldFetchParameters parameters) {
        PamirsSession.getMessageHub().clear();
        PamirsSession.getMessageHub().setPath(GraphQLUtils.convertPath(parameters.getExecutionStepInfo().getPath()));
        return whenCompleted((result, t) -> {
            fillInternalMessageHub(parameters.getInstrumentationState(), t);
            PamirsSession.getMessageHub().clear();
        });
    }

    @Override
    public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult, InstrumentationExecutionParameters parameters) {
        //
        // this allows you to instrument the execution result some how.  For example the Tracing support uses this to put
        // the `extensions` map of data in place
        //
        Map<Object, Object> extensions = executionResult.getExtensions();
        List<GraphQLError> errors = executionResult.getErrors();
        boolean deferredResult = executionResult instanceof DeferredExecutionResultImpl;
        boolean commonResult = executionResult instanceof ExecutionResultImpl;
        if (deferredResult || commonResult) {
            extensions = generateTotalExtensions(parameters, extensions);
            errors = generateTotalErrors(parameters, errors);
        }
        ExecutionResult resultWithExtensions = new ExecutionResultImpl(executionResult.getData(), errors, extensions);
        if (deferredResult) {
            executionResult = DeferredExecutionResultImpl.newDeferredExecutionResult()
                    .path(ExecutionPath.fromList(((DeferredExecutionResult) executionResult).getPath())).from(resultWithExtensions).build();
        } else if (commonResult) {
            executionResult = resultWithExtensions;
        } else {
            logger.info("The result is not known implementation. Clazz:" + executionResult.getClass());
        }
        PamirsSession.getMessageHub().clear();
        return CompletableFuture.completedFuture(executionResult);
    }

    private Map<Object, Object> generateTotalExtensions(InstrumentationExecutionParameters parameters, Map<Object, Object> extensions) {
        if (null == extensions) {
            extensions = new LinkedHashMap<>();
        }
        MessageHubInstrumentationState state = parameters.getInstrumentationState();
        extensions.put(ResultExtensionsConstants.SUCCESS, state.getMessageHub().isSuccess());
        List<Message> messageList = state.getMessageHub().getDataMessages();
        if (null != messageList) {
            extensions.put(ResultExtensionsConstants.MESSAGES, messageList);
        }
        Map<Object, Object> extra = state.getMessageHub().getExtensions();
        if (null != extra) {
            extensions.put(ResultExtensionsConstants.EXTRA, extra);
        }
        Set<String> directives = state.getMessageHub().getDirectives();
        if (null != directives) {
            extensions.put(ResultExtensionsConstants.DIRECTIVES, directives);
        }
        return extensions;
    }

    private List<GraphQLError> generateTotalErrors(InstrumentationExecutionParameters parameters, List<GraphQLError> errors) {
        MessageHubInstrumentationState state = parameters.getInstrumentationState();
        if (!state.getMessageHub().isSuccess() && !state.getMessageHub().isException()) {
            ErrorExtension errorExtension = state.getMessageHub().getErrorExtension();
            if (null != errorExtension) {
                if (CollectionUtils.isEmpty(errors)) {
                    errors = new ArrayList<>();
                }
                errors.add(ClientGraphQLError.build(errorExtension));
            }
        }
        return errors;
    }

    private void fillInternalMessageHub(MessageHubInstrumentationState state, Throwable t) {
        boolean success = PamirsSession.getMessageHub().isSuccess();
        if (!success) {
            state.getMessageHub().setSuccess(false);
        }
        if (t instanceof PamirsException) {
            List<Message> errorMessages = PamirsSession.getMessageHub().getErrorMessages();
            ((PamirsException) t).setMsgDetail(errorMessages);
            state.getMessageHub().setException(true);
        } else if (!success) {
            state.getMessageHub().fill(false, PamirsSession.getMessageHub().getErrorExtension());
        }
        List<Message> messageList = PamirsSession.getMessageHub().getDataMessages();
        if (null != messageList) {
            state.getMessageHub().msg(messageList);
        }
        Map<Object, Object> extensions = PamirsSession.getMessageHub().getExtensions();
        if (null != extensions) {
            state.getMessageHub().extensions(extensions);
        }
        Set<String> directives = PamirsSession.getMessageHub().getDirectives();
        if (null != directives) {
            state.getMessageHub().directives(directives);
        }
    }

}