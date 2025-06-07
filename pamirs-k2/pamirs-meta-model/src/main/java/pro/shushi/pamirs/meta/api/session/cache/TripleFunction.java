package pro.shushi.pamirs.meta.api.session.cache;

/**
 * TripleFunction
 * <p>
 * 2021/8/19 1:00 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@FunctionalInterface
public interface TripleFunction<T, U, X, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @param x the third function argument
     * @return the function result
     */
    R apply(T t, U u, X x);

}
