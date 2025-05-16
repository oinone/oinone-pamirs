package pro.shushi.pamirs.core.common;

import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

import java.io.Serializable;
import java.util.Optional;

/**
 * RpcResult
 *
 * @author yakir on 2022/11/11 11:32.
 */
public class RpcResult<T extends Serializable> {

    private T       data;
    private boolean success = true;
    private String  errorMsg;

    public RpcResult() {
    }

    public static <T extends Serializable> RpcResult<T> of(T data) {
        RpcResult<T> result = new RpcResult<>();
        result.setData(data);
        return result;
    }

    public static <T extends Serializable> RpcResult<T> ofError(ExpBaseEnum expBaseEnum) {
        RpcResult<T> result = new RpcResult<>();
        result.setSuccess(false);
        result.setErrorMsg(expBaseEnum.msg());
        return result;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public RpcResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public RpcResult<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public RpcResult<T> setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public Optional<T> getData() {
        return Optional.ofNullable(data);
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
