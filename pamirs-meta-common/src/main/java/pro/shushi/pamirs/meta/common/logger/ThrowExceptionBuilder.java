package pro.shushi.pamirs.meta.common.logger;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

public class ThrowExceptionBuilder implements IThrowExceptionBuilder<PamirsException> {

    private int code;
    private String type;
    private String msg;
    private StringBuilder msgBuilder;
    private Object extend = "";//扩展数据
    private Throwable e;

    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder(T expEnum, Throwable e) {
        this.code = expEnum.code();
        this.type = expEnum.type().type;
        this.msg = expEnum.msg();
        this.msgBuilder = new StringBuilder();
        this.e = e;
    }

    @Override
    public ThrowExceptionBuilder appendMsg(String otherMsg) {
        this.msgBuilder.append(otherMsg);
        return this;
    }

    @Override
    public ThrowExceptionBuilder setExtendObject(Object object) {
        this.extend = object;
        return this;
    }

    @Override
    public PamirsException errThrow() {
        if (e == null) {
            String msg = this.msg;
            String otherMsg = msgBuilder.toString();
            if (StringUtils.isNotBlank(otherMsg))
                msg = msg.concat(" : ").concat(otherMsg);
            return new PamirsException(this.code, this.type, msg, this.extend);
        } else {
            if (e instanceof PamirsException)
                return (PamirsException) e;
            if (e instanceof UndeclaredThrowableException) {
                Throwable targetException = ((InvocationTargetException) e.getCause()).getTargetException();
                if (targetException instanceof PamirsException)
                    return (PamirsException) targetException;
            }
            if (e instanceof InvocationTargetException) {
                Throwable targetException = ((InvocationTargetException) e).getTargetException();
                if (targetException instanceof PamirsException)
                    return (PamirsException) targetException;
            }
            return new PamirsException(this.code, this.type, this.msg, this.extend, e);
        }
    }
}