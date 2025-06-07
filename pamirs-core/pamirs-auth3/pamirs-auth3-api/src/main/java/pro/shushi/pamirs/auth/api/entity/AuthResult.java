package pro.shushi.pamirs.auth.api.entity;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 权限结果
 *
 * @author Adamancy Zhang at 17:16 on 2024-01-06
 */
public class AuthResult<T> implements Serializable {

    private static final long serialVersionUID = -135107692047134519L;

    /**
     * 是否成功获取
     */
    private final boolean isFetch;

    /**
     * 获取数据
     */
    private final T data;

    private AuthResult(boolean isFetch, T data) {
        this.isFetch = isFetch;
        this.data = data;
    }

    public static <V> AuthResult<V> success() {
        return new AuthResult<>(true, null);
    }

    public static <V> AuthResult<V> success(V value) {
        return new AuthResult<>(true, value);
    }

    public static <V> AuthResult<V> error() {
        return new AuthResult<>(false, null);
    }

    public boolean isFetch() {
        return isFetch;
    }

    public T getData() {
        return data;
    }

    @SuppressWarnings("unchecked")
    public <R> AuthResult<R> transfer(Function<T, R> function) {
        if (this.isFetch) {
            if (this.data == null) {
                return (AuthResult<R>) this;
            }
            return AuthResult.success(function.apply(this.data));
        }
        return (AuthResult<R>) this;
    }
}
