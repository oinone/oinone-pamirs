package pro.shushi.pamirs.middleware.schedule.constant;

public enum ErrorCodeConstants {


    BKCYCLE88888888(88888888, "BaseScheduleTask.CycleTask.Finished", "循环任务根据完成条件跳出循环"),
    ;

    /**
     * 错误code
     */
    private int errorCode;

    /**
     * 错误名称
     */
    private String errorName;

    /**
     * 错误信息
     */
    private String errorMessage;

    ErrorCodeConstants(int errorCode, String errorName, String errorMessage) {

        this.errorCode = errorCode;
        this.errorName = errorName;
        this.errorMessage = errorMessage;

    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage(Object... args) {
        return String.format(errorMessage, args);
    }

}
