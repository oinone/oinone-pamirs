package pro.shushi.pamirs.eip.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * 集成接口响应体
 *
 * @author Adamancy Zhang at 10:33 on 2024-04-10
 */
public class EipIntegrationResponse implements Serializable {

    private static final long serialVersionUID = -6272703969282075964L;

    private boolean success;

    private String errorCode;

    private String errorMessage;

    @JSONField(serialize = false)
    private transient EipResult<?> response;

    public EipIntegrationResponse() {
        this.success = true;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public EipResult<?> getResponse() {
        return response;
    }

    public void setResponse(EipResult<?> response) {
        this.response = response;
    }

    protected static <T extends EipIntegrationResponse> T returnResult(EipResult<T> result, Class<T> clazz, Supplier<T> supplier) {
        if (result.getSuccess()) {
            T response = result.getResult(clazz);
            response.setResponse(result);
            return response;
        }
        T response = supplier.get();
        response.setSuccess(false);
        response.setErrorCode(result.getErrorCode());
        response.setErrorMessage(result.getErrorMessage());
        response.setResponse(result);
        return response;
    }
}
