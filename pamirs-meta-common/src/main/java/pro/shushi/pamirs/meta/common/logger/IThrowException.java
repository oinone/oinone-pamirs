package pro.shushi.pamirs.meta.common.logger;

public abstract class IThrowException extends RuntimeException {

    protected int code;
    protected String type;
    protected String msg;
    protected Object extend;//扩展数据

    protected IThrowException(int code, String type, String msg, Object extend) {
        super("code: " + code + ", type: " + type + ", msg: " + msg + ", extend: " + extend);
        this.code = code;
        this.type = type;
        this.msg = msg;
        this.extend = extend;
    }

    protected IThrowException(int code, String type, String msg, Object extend, Throwable e) {
        super("code: " + code + ", type: " + type + ", msg: " + msg + ", extend: " + extend, e);
        this.code = code;
        this.type = type;
        this.msg = msg;
        this.extend = extend;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public Object getExtend() {
        return extend;
    }

    @Override
    public String toString() {
        return "PamirsException code: " + code + ", type: " + type + ", msg: " + msg + ", extend: " + extend;
    }
}
