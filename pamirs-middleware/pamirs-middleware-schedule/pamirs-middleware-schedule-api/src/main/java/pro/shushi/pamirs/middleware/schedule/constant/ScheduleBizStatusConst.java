package pro.shushi.pamirs.middleware.schedule.constant;

public enum ScheduleBizStatusConst {

    DEFAULT("DEFAULT", 0, "未处理"),
    SUCCESS("INITIALIZE", 1, "成功"),
    RETRY("RETRY", 2, "失败需要重试"),
    ERROR("ERROR", 3, "失败不需要重试");

    private String value;
    private int code;
    private String msg;

    ScheduleBizStatusConst(String value, int code, String msg) {
        this.value = value;
        this.code = code;
        this.msg = msg;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
