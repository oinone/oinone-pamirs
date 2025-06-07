package pro.shushi.pamirs.middleware.canal.entity;

import java.io.Serializable;

/**
 * @author Adamancy Zhang
 * @date 2020-12-15 17:56
 */
public class SimpleResultEntity<T> extends SimpleMessageEntity implements Serializable {

    private static final long serialVersionUID = 1357778252868913260L;

    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public SimpleResult<T> fetch() {
        if (getSuccess()) {
            return SimpleResult.success(result);
        } else {
            return SimpleResult.failure(getMessage(), result);
        }
    }
}
