package pro.shushi.pamirs.eip.api.entity.openapi;

import pro.shushi.pamirs.eip.api.auth.OpenApiConstant;

/**
 * @author Adamancy Zhang at 14:09 on 2021-02-24
 */
public class SimpleResponseBody<T> {

    public static final int DEFAULT_SUCCESS_CODE = OpenApiConstant.OPEN_API_ERROR_CODE_SUCCESS_VALUE;

    public static final String DEFAULT_SUCCESS_MESSAGE = OpenApiConstant.OPEN_API_ERROR_MSG_SUCCESS_VALUE;

    private Boolean success;

    private int errorCode = DEFAULT_SUCCESS_CODE;

    private String errorMsg;

    private T data;

    public SimpleResponseBody() {
    }

    public SimpleResponseBody(T data) {
        this.success = true;
        this.data = data;
    }

    public SimpleResponseBody(Boolean success, int errorCode, String errorMsg, T data) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public SimpleResponseBody<T> setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public SimpleResponseBody<T> setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public SimpleResponseBody<T> setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public T getData() {
        return data;
    }

    public SimpleResponseBody<T> setData(T data) {
        this.data = data;
        return this;
    }

    public static <T> SimpleResponseBody<T> error(Integer errorCode, String errorMsg) {
        return new SimpleResponseBody<>(false, errorCode, errorMsg, null);
    }

    public static <T> SimpleResponseBody<T> success() {
        return new SimpleResponseBody<>(true, DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, null);
    }

    public static <T> SimpleResponseBody<T> success(T data) {
        return new SimpleResponseBody<>(true, DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, data);
    }
}
