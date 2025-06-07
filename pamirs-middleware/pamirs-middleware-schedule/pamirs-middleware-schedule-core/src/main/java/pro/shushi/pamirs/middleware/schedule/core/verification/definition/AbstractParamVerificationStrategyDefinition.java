package pro.shushi.pamirs.middleware.schedule.core.verification.definition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamVerificationStrategyDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.enumeration.ParamVerificationExceptionHandlerType;
import pro.shushi.pamirs.middleware.schedule.core.verification.exception.VerificationException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 16:11
 */
public abstract class AbstractParamVerificationStrategyDefinition<T> implements IParamVerificationStrategyDefinition<T> {

    protected static final Logger logger = LoggerFactory.getLogger(IParamVerificationStrategyDefinition.class);

    private static final String DEFAULT_ERROR_MESSAGE = "param validation failed. key = ";

    private static final List<ParamVerificationExceptionHandlerType> DEFAULT_HANDLER_TYPES;

    static {
        DEFAULT_HANDLER_TYPES = new ArrayList<>();
        DEFAULT_HANDLER_TYPES.add(ParamVerificationExceptionHandlerType.THROW_EXCEPTION);
    }

    private final List<ParamVerificationExceptionHandlerType> handlerTypes;

    private final int handlerTypesValue;

    public AbstractParamVerificationStrategyDefinition() {
        this(DEFAULT_HANDLER_TYPES);
    }

    public AbstractParamVerificationStrategyDefinition(List<ParamVerificationExceptionHandlerType> handlerTypes) {
        this.handlerTypes = handlerTypes;
        int finalHandlerTypesValue = 0;
        for (ParamVerificationExceptionHandlerType handlerType : handlerTypes) {
            finalHandlerTypesValue += handlerType.intValue();
        }
        this.handlerTypesValue = finalHandlerTypesValue;
    }

    @Override
    public List<ParamVerificationExceptionHandlerType> getHandlerTypes() {
        return handlerTypes;
    }

    @Override
    public int getHandlerTypesValue() {
        return handlerTypesValue;
    }

    @Override
    public boolean verify(String key, T value) {
        if (!verifyHandler(value)) {
            exceptionHandler(key, value);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    protected void exceptionHandler(String key, T value) {
        if ((this.handlerTypesValue & ParamVerificationExceptionHandlerType.THROW_EXCEPTION.intValue()) != 0) {
            throw throwException(key, value);
        } else if ((this.handlerTypesValue & ParamVerificationExceptionHandlerType.LOG_ERROR.intValue()) != 0) {
            logError(key, value);
        } else if ((this.handlerTypesValue & ParamVerificationExceptionHandlerType.LOG_WARN.intValue()) != 0) {
            logWarn(key, value);
        } else if ((this.handlerTypesValue & ParamVerificationExceptionHandlerType.LOG_DEBUG.intValue()) != 0) {
            logDebug(key, value);
        }
    }

    protected String getErrorMessage(String key, T value) {
        return DEFAULT_ERROR_MESSAGE + key + ". strategy: " + getStrategyTip();
    }

    protected void logDebug(String key, T value) {
        logger.debug(getErrorMessage(key, value));
    }

    protected void logWarn(String key, T value) {
        logger.warn(getErrorMessage(key, value));
    }

    protected void logError(String key, T value) {
        logger.error(getErrorMessage(key, value));
    }

    protected VerificationException throwException(String key, T value) {
        return new VerificationException(getErrorMessage(key, value), key, value);
    }

    /**
     * verify handler method.(verify call this)
     *
     * @param value verified value
     * @return verification result
     */
    protected abstract boolean verifyHandler(T value);
}
