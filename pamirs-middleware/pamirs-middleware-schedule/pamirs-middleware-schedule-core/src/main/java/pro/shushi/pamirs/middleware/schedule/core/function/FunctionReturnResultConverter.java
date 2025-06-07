package pro.shushi.pamirs.middleware.schedule.core.function;

/**
 * function return result converter
 *
 * @author Adamancy Zhang
 * @date 2020-10-22 11:21
 */
@FunctionalInterface
public interface FunctionReturnResultConverter<T> {

    /**
     * convert method.
     *
     * @param value return result
     * @return convert result
     */
    T convert(Object value);
}
