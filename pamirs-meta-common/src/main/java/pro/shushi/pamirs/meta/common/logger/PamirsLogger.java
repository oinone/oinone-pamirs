package pro.shushi.pamirs.meta.common.logger;

import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

public interface PamirsLogger<E extends IThrowException, D extends IThrowExceptionBuilder<E>> {

    void trace(String msg);

    void info(String msg);

    void debug(String msg);

    void warn(String msg);

    void error(String msg);

    void trace(String format, Object... objects);

    void info(String format, Object... objects);

    void debug(String format, Object... objects);

    void warn(String format, Object... objects);

    void error(String format, Object... objects);

    boolean isTraceEnabled();

    boolean isDebugEnabled();

    boolean isInfoEnabled();

    boolean isWarnEnabled();

    boolean isErrorEnabled();

    <T extends Enum & ExpBaseEnum> D info(T expEnum);

    <T extends Enum & ExpBaseEnum> D info(T expEnum, String format, Object... objects);

    <T extends Enum & ExpBaseEnum> D info(T expEnum, Throwable e);

    <T extends Enum & ExpBaseEnum> D info(T expEnum, Throwable e, String format, Object... objects);

    <T extends Enum & ExpBaseEnum> D debug(T expEnum);

    <T extends Enum & ExpBaseEnum> D debug(T expEnum, String format, Object... objects);

    <T extends Enum & ExpBaseEnum> D debug(T expEnum, Throwable e);

    <T extends Enum & ExpBaseEnum> D debug(T expEnum, Throwable e, String format, Object... objects);

    <T extends Enum & ExpBaseEnum> D warn(T expEnum);

    <T extends Enum & ExpBaseEnum> D warn(T expEnum, String format, Object... objects);

    <T extends Enum & ExpBaseEnum> D warn(T expEnum, Throwable e);

    <T extends Enum & ExpBaseEnum> D warn(T expEnum, Throwable e, String format, Object... objects);

    <T extends Enum & ExpBaseEnum> D error(T expEnum);

    <T extends Enum & ExpBaseEnum> D error(T expEnum, String format, Object... objects);

    <T extends Enum & ExpBaseEnum> D error(T expEnum, Throwable e);

    <T extends Enum & ExpBaseEnum> D error(T expEnum, Throwable e, String format, Object... objects);
}
