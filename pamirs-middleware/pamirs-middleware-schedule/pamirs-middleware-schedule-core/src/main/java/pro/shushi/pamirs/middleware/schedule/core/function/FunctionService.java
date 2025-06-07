package pro.shushi.pamirs.middleware.schedule.core.function;

import pro.shushi.pamirs.middleware.schedule.core.function.model.FunctionDefinition;

/**
 * function service
 *
 * @author Adamancy Zhang
 * @date 2020-10-22 09:51
 */
public interface FunctionService {

    /**
     * execute function and return result.
     *
     * @param functionDefinition function definition
     * @param args               function args
     * @return result
     */
    <T> T execute(FunctionDefinition<T> functionDefinition, Object... args);

    /**
     * execute function without result.
     *
     * @param functionDefinition function definition
     * @param args               function args
     */
    void executeWithoutResult(FunctionDefinition<Void> functionDefinition, Object... args);

    /**
     * register remote function.
     *
     * @param functionDefinition function definition
     */
    default void registerFunction(FunctionDefinition<?> functionDefinition) {
        throw new UnsupportedOperationException();
    }

    /**
     * cancel remote function.
     *
     * @param functionDefinition function definition
     */
    default void cancelFunction(FunctionDefinition<?> functionDefinition) {
        throw new UnsupportedOperationException();
    }
}
