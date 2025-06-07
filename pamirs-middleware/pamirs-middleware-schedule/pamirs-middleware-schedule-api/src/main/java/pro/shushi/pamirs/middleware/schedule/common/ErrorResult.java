package pro.shushi.pamirs.middleware.schedule.common;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class ErrorResult implements Serializable {


    private static final long serialVersionUID = -4477440770116680900L;

    public static final int GENERAL_CODE = 999;
    public static final String GENERAL_NAME = "SERVICE_ERROR";

    private int errorCode;
    private String errorName;
    private String errorMessage;

    public ErrorResult() {
        this(999, "SERVICE_ERROR", (String) null);
    }

    public ErrorResult(int code, String name, String message) {
        this.setError(code, name, message);
    }

    public void setError(int code, String name, String message) {
        this.errorCode = code;
        this.errorName = name;
        this.errorMessage = message;
    }

    public void setError(String name, String message) {
        this.setError(999, name, message);
    }

    public boolean hasError() {
        return StringUtils.isNotBlank(this.errorMessage)
                && StringUtils.isNotBlank(this.errorName);
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorName() {
        return this.errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
