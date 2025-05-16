package pro.shushi.pamirs.middleware.schedule.exception;

/**
 * @author Adamancy Zhang
 * @date 2020-10-22 18:40
 */
public abstract class ScheduleException extends RuntimeException {

    private Code code;

    ScheduleException(Code code, String message) {
        super(message);
        this.code = code;
    }

    ScheduleException(Code code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * We hope that all {@link ScheduleException} should be created from here.
     *
     * @param code {@link Code}
     * @return {@link ScheduleException}
     */
    public static ScheduleException create(Code code) {
        return null;
    }

    /**
     * We hope that all {@link ScheduleException} should be created from here.
     *
     * @param code  {@link Code}
     * @param cause {@link Throwable}
     * @return {@link ScheduleException}
     */
    public static ScheduleException create(Code code, Throwable cause) {
        return null;
    }

    public enum Code {

        /**
         * Everything is ok. (You shouldn't use it).
         *
         * @see ScheduleException#create
         */
        OK(0, "Ok");

        private final int code;

        private final String message;

        Code(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int intValue() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
