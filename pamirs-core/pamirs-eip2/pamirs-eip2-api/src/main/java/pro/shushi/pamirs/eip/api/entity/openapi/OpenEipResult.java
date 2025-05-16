package pro.shushi.pamirs.eip.api.entity.openapi;

import pro.shushi.pamirs.eip.api.auth.OpenApiConstant;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * @Author: rainc
 * @Date: 2021/4/6 上午11:45
 * @Description: 开放接口返回值封装
 */
@Data
public class OpenEipResult<T> {
    private Boolean success;

    private String errorCode;

    private String errorMsg;

    private T data;

    public OpenEipResult() {
    }

    public OpenEipResult(T data) {
        this.success = true;
        this.data = data;
        this.errorCode = OpenApiConstant.OPEN_API_ERROR_CODE_SUCCESS_VALUE + "";
        this.errorMsg = OpenApiConstant.OPEN_API_ERROR_MSG_SUCCESS_VALUE;
    }

    public OpenEipResult(String errorCode, String errorMsg) {
        this.success = false;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public static <T> OpenEipResult<T> success(T result) {
        return new OpenEipResult<>(result);
    }

    public static <T> OpenEipResult<T> error(String errorCode, String errorMsg) {
        return new OpenEipResult<>(errorCode, errorMsg);
    }
}
