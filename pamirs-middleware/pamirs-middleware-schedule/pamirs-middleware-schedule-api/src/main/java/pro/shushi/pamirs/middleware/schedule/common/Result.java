package pro.shushi.pamirs.middleware.schedule.common;

import java.io.Serializable;

public class Result<T> extends ErrorResult implements Serializable {

    private static final long serialVersionUID = -8722768053248374040L;

    private boolean success = true;
    private T data;

    public Result() {
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setFail(String errorMessage) {
        success = false;
        setErrorMessage(errorMessage);
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", data=" + data +
                ", errorCode='" + getErrorCode() + '\'' +
                ", errorMessage='" + getErrorMessage() + '\'' +
                '}';
    }
}
