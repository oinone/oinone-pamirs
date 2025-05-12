package pro.shushi.pamirs.meta.common.exception;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.AppName;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.util.PStringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

public class PamirsException extends RuntimeException {

    private static final long serialVersionUID = 1353138041604034025L;

    private final int code;
    private final String applicationName;
    private final String type;
    private final String msg;
    private final String extra;
    private Object msgDetail;
    private final String level;
    private final Object extend;//扩展数据

    private PamirsException(int code, String type, String msg, String extra, String level, Object extend) {
        super("code: " + code + ", type: " + type + ", msg: " + msg + ", extra: " + extra + ", extend: " + extend + ", applicationName: " + getAppName());
        this.code = code;
        this.type = type;
        this.msg = msg;
        this.extra = extra;
        this.level = level;
        this.extend = extend;
        this.applicationName = getAppName();
    }

    private PamirsException(int code, String type, String msg, String extra, String level, Object extend, Throwable e) {
        super("code: " + code + ", type: " + type + ", msg: " + msg + ", extra: " + extra + ", extend: " + extend + ", applicationName: " + getAppName(), e);
        this.code = code;
        this.type = type;
        this.msg = msg;
        this.extra = extra;
        this.level = level;
        this.extend = extend;
        this.applicationName = getAppName();
    }

    public static String getAppName() {
        return AppName.get();
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

    public String getLevel() {
        return level;
    }

    public Object getExtend() {
        return extend;
    }

    public Object getMsgDetail() {
        return msgDetail;
    }

    public void setMsgDetail(Object detail) {
        msgDetail = detail;
    }

    @Override
    public String getMessage() {
        String message = msg;
        if (StringUtils.isNotBlank(extra)) {
            if (StringUtils.isNotBlank(msg)) {
                message = msg.concat(", ").concat(extra);
            } else {
                message = extra;
            }
        }
        return message;
    }

    public static <T extends Enum<T> & ExpBaseEnum> Builder<T> construct(T expEnum) {
        return new Builder<>(expEnum, null);
    }

    public static <T extends Enum<T> & ExpBaseEnum> Builder<T> construct(T expEnum, Object... args) {
        return new Builder<>(expEnum, null, args);
    }

    public static <T extends Enum<T> & ExpBaseEnum> Builder<T> construct(T expEnum, Throwable e) {
        return new Builder<>(expEnum, e);
    }

    @Override
    public String toString() {
        return "PamirsException level: " + level + ", code: " + code + ", type: " + type + ", msg: " + msg + ", extra:" + extra + ", extend: " + extend + ", applicationName: " + applicationName;
    }

    public static class Builder<T extends Enum<T> & ExpBaseEnum> {

        private final int code;
        private final String type;
        private String msg;
        private String level;
        private final StringBuilder msgBuilder;
        private Object extend;
        private final Throwable e;

        private Builder(T expEnum, Throwable e) {
            this.code = expEnum.code();
            this.type = expEnum.type().getType();
            this.msg = expEnum.msg();
            this.level = ExpBaseEnum.LEVEL.ERROR.name();
            this.extend = null;
            this.msgBuilder = new StringBuilder();
            this.e = e;
        }

        private Builder(T expEnum, Throwable e, Object... args) {
            this.code = expEnum.code();
            this.type = expEnum.type().getType();
            this.msg = PStringUtils.parse1(expEnum.msg(), args);
            this.level = ExpBaseEnum.LEVEL.ERROR.name();
            this.extend = null;
            this.msgBuilder = new StringBuilder();
            this.e = e;
        }

        public Builder<T> setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder<T> setLevel(ExpBaseEnum.LEVEL level) {
            this.level = level.name();
            return this;
        }

        public Builder<T> debug() {
            this.level = ExpBaseEnum.LEVEL.DEBUG.name();
            return this;
        }

        public Builder<T> info() {
            this.level = ExpBaseEnum.LEVEL.INFO.name();
            return this;
        }

        public Builder<T> warn() {
            this.level = ExpBaseEnum.LEVEL.WARN.name();
            return this;
        }

        public Builder<T> error() {
            this.level = ExpBaseEnum.LEVEL.ERROR.name();
            return this;
        }

        public Builder<T> appendMsg(String otherMsg) {
            this.msgBuilder.append(otherMsg);
            return this;
        }

        public Builder<T> setExtendObject(Object object) {
            this.extend = object;
            return this;
        }

        public PamirsException errThrow() {
            String extra = msgBuilder.toString();
            if (e == null) {
                return new PamirsException(this.code, this.type, this.msg, extra, this.level, this.extend);
            } else {
                if (e instanceof PamirsException) {
                    return (PamirsException) e;
                }
                if (e instanceof UndeclaredThrowableException) {
                    Throwable targetException = e.getCause();
                    if (targetException instanceof InvocationTargetException) {
                        Throwable finalException = ((InvocationTargetException) targetException).getTargetException();
                        if (finalException instanceof PamirsException) {
                            return (PamirsException) finalException;
                        }
                    } else if (targetException instanceof PamirsException) {
                        return (PamirsException) targetException;
                    }
                }
                if (e instanceof InvocationTargetException) {
                    Throwable targetException = ((InvocationTargetException) e).getTargetException();
                    if (targetException instanceof PamirsException) {
                        return (PamirsException) targetException;
                    }
                }
                return new PamirsException(this.code, this.type, this.msg, extra, this.level, this.extend, e);
            }
        }
    }
}