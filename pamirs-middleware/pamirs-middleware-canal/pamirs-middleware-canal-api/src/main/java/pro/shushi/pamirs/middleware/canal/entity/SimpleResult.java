package pro.shushi.pamirs.middleware.canal.entity;

import java.io.Serializable;

/**
 * @author Adamancy Zhang
 * @date 2020-11-13 15:42
 */
public class SimpleResult<T> extends SimpleReadonlyMessage implements Serializable {

    private static final long serialVersionUID = -4524726898459129718L;

    private static final String DEFAULT_SUCCESS_MESSAGE = "ok";

    private final T result;

    private SimpleResult(boolean success, String message, T result) {
        super(success, message);
        this.result = result;
    }

    public static <T> SimpleResult<T> success() {
        return new SimpleResult<>(true, DEFAULT_SUCCESS_MESSAGE, null);
    }

    public static <T> SimpleResult<T> success(T result) {
        return new SimpleResult<>(true, DEFAULT_SUCCESS_MESSAGE, result);
    }

    public static <T> SimpleResult<T> success(String message, T result) {
        return new SimpleResult<>(true, message, result);
    }

    public static <T> SimpleResult<T> failure(String message) {
        return new SimpleResult<>(false, message, null);
    }

    public static <T> SimpleResult<T> failure(String message, T result) {
        return new SimpleResult<>(false, message, result);
    }

    public T getResult() {
        return result;
    }
}
