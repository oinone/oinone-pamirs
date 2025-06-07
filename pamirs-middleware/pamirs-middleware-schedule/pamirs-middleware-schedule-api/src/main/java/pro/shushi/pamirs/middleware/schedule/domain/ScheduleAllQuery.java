package pro.shushi.pamirs.middleware.schedule.domain;

public class ScheduleAllQuery extends ScheduleQuery {

    private static final long serialVersionUID = 2915739164007874397L;

    private String param;
    private String errorLog;

    public String getParam() {
        return param;
    }

    public ScheduleAllQuery setParam(String param) {
        this.param = param;
        return this;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public ScheduleAllQuery setErrorLog(String errorLog) {
        this.errorLog = errorLog;
        return this;
    }
}
