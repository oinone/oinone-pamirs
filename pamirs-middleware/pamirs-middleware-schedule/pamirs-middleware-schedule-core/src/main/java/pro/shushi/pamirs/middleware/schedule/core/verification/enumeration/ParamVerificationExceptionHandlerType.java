package pro.shushi.pamirs.middleware.schedule.core.verification.enumeration;

/**
 * param verification exception handler type.
 *
 * @author Adamancy Zhang
 * @date 2020-10-20 13:41
 */
public enum ParamVerificationExceptionHandlerType {

    /**
     * do nothing.
     */
    NONE(0),

    /**
     * write debug log.
     */
    LOG_DEBUG(1),

    /**
     * write warn log.
     */
    LOG_WARN(2),

    /**
     * write error log.
     */
    LOG_ERROR(4),

    /**
     * throw runtime exception.
     */
    THROW_EXCEPTION(8);

    private final int value;

    ParamVerificationExceptionHandlerType(int value) {
        this.value = value;
    }

    public int intValue() {
        return value;
    }
}