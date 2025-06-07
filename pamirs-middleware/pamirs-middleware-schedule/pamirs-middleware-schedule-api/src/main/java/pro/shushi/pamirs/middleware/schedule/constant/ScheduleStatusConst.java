package pro.shushi.pamirs.middleware.schedule.constant;

public enum ScheduleStatusConst {

    DEFAULT("DEFAULT", (byte) 0, "初始状态"),
    EXECUTE("EXECUTE", (byte) 1, "可执行状态"),
    DELETE("DELETE", (byte) -1, "删除"),

    ;

    private String value;
    private Byte code;
    private String msg;

    ScheduleStatusConst(String value, Byte code, String msg) {
        this.value = value;
        this.code = code;
        this.msg = msg;
    }

    public String getValue() {
        return value;
    }

    public Byte getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
