package pro.shushi.pamirs.meta.common.logger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

import java.util.function.BiConsumer;

public class Log implements PamirsLogger<PamirsException, ThrowExceptionBuilder> {

    private Logger logger;

    private Log(Logger logger) {
        this.logger = logger;
    }

    public static PamirsLogger<PamirsException, ThrowExceptionBuilder> newInstance(Class cls) {
        return new Log(LoggerFactory.getLogger(cls));
    }

    public static PamirsLogger<PamirsException, ThrowExceptionBuilder> newInstance(String className) {
        return new Log(LoggerFactory.getLogger(className));
    }

    public static PamirsLogger<PamirsException, ThrowExceptionBuilder> get(Logger logger) {
        return new Log(logger);
    }

    @Override
    public void trace(String msg) {
        this.logger.trace(msg);
    }

    @Override
    public void info(String msg) {
        this.logger.info(msg);
    }

    @Override
    public void debug(String msg) {
        this.logger.debug(msg);
    }

    @Override
    public void warn(String msg) {
        this.logger.warn(msg);
    }

    @Override
    public void error(String msg) {
        this.logger.error(msg);
    }

    @Override
    public void trace(String format, Object... objects) {
        this.logger.trace(format, objects);
    }

    @Override
    public void info(String format, Object... objects) {
        this.logger.info(format, objects);
    }

    @Override
    public void debug(String format, Object... objects) {
        this.logger.debug(format, objects);
    }

    @Override
    public void warn(String format, Object... objects) {
        this.logger.warn(format, objects);
    }

    @Override
    public void error(String format, Object... objects) {
        this.logger.error(format, objects);
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder info(T expEnum) {
        return info(expEnum, null, null, null);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder info(T expEnum, String format, Object... objects) {
        return info(expEnum, null, format, objects);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder info(T expEnum, Throwable e) {
        return info(expEnum, e, null);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder info(T expEnum, Throwable e, String format, Object... objects) {
        return getThrowExceptionTemplate(expEnum, e, true, (_log, _objects) -> this.logger.info(_log, _objects), format, objects);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder debug(T expEnum) {
        return debug(expEnum, null, null, null);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder debug(T expEnum, String format, Object... objects) {
        return debug(expEnum, null, format, objects);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder debug(T expEnum, Throwable e) {
        return debug(expEnum, e, null);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder debug(T expEnum, Throwable e, String format, Object... objects) {
        return getThrowExceptionTemplate(expEnum, e, true, (_log, _objects) -> this.logger.debug(_log, _objects), format, objects);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder warn(T expEnum) {
        return warn(expEnum, null, null, null);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder warn(T expEnum, String format, Object... objects) {
        return warn(expEnum, null, format, objects);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder warn(T expEnum, Throwable e) {
        return warn(expEnum, e, null);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder warn(T expEnum, Throwable e, String format, Object... objects) {
        return getThrowExceptionTemplate(expEnum, e, true, (_log, _objects) -> this.logger.warn(_log, _objects), format, objects);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder error(T expEnum) {
        return error(expEnum, null, null, null);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder error(T expEnum, String format, Object... objects) {
        return error(expEnum, null, format, objects);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder error(T expEnum, Throwable e) {
        return error(expEnum, e, null);
    }

    @Override
    public <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder error(T expEnum, Throwable e, String format, Object... objects) {
        return getThrowExceptionTemplate(expEnum, e, true, (_log, _objects) -> this.logger.error(_log, _objects), format, objects);
    }

    private void printfTemplate(int code, String type, String msg, Throwable e, String format, Object[] objects, BiConsumer<String, Object[]> consumer) {
        StringBuilder sb = new StringBuilder();
        if (e instanceof PamirsException)
            sb.append(e.getMessage());
        else
            sb.append("code: ").append(code).append(", type: ").append(type).append(", msg: ").append(msg);
        if (StringUtils.isNotBlank(format) && objects != null) {
            sb.append(", other info: ").append(format);
        } else {
            sb.append(", other info: ").append(format);
        }
        consumer.accept(sb.toString(), objects);
    }

    private <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder getThrowExceptionTemplate(T expEnum, Throwable e, Boolean isPrint, BiConsumer<String, Object[]> consumer, String format, Object... objects) {
        Assert.notNull(expEnum, "ExpBaseEnum must be not null");
        if (isPrint)
            printfTemplate(expEnum.code(), expEnum.type().type, expEnum.msg(), e, format, objects, consumer);
        return new ThrowExceptionBuilder(expEnum, e);
    }
}
