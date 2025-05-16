package pro.shushi.pamirs.framework.gateways.graph.instrument;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

public class CustomInstrumentation extends SimpleInstrumentation {

    private final static Logger logger = LoggerFactory.getLogger(CustomInstrumentation.class);

    @Override
    public InstrumentationState createState() {
        //
        // instrumentation state is passed during each invocation of an Instrumentation method
        // and allows you to put stateful data away and reference it during the query execution
        //
        return new CustomInstrumentationState();
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
        long startNanos = System.nanoTime();
        return new SimpleInstrumentationContext<ExecutionResult>() {
            @Override
            public void onCompleted(ExecutionResult result, Throwable t) {
                CustomInstrumentationState state = parameters.getInstrumentationState();
                long takeTime = System.nanoTime() - startNanos;
                state.recordTiming(parameters.getQuery(), takeTime);
                logger.debug(MessageFormat.format("schema:{0},take time:{1}", parameters.getQuery(), takeTime));
            }
        };
    }

    @Override
    public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult, InstrumentationExecutionParameters parameters) {
        //
        // this allows you to instrument the execution result some how.  For example the Tracing support uses this to put
        // the `extensions` map of data in place
        //
        return CompletableFuture.completedFuture(executionResult);
    }
}