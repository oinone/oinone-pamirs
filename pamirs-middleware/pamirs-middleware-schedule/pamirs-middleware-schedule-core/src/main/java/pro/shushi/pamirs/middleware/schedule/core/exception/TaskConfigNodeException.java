package pro.shushi.pamirs.middleware.schedule.core.exception;

/**
 * task configuration node exception.
 *
 * @author Adamancy Zhang
 * @date 2020-10-20 11:38
 */
public abstract class TaskConfigNodeException extends Exception {

    private static final String ERROR_MESSAGE_PREFIX = "TaskConfigNodeErrorCode = ";

    private final Code code;

    private String path;

    TaskConfigNodeException(Code code) {
        this(code, null);
    }

    TaskConfigNodeException(Code code, String path) {
        this.code = code;
        this.path = path;
    }

    public Code getCode() {
        return code;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String getMessage() {
        if (path == null) {
            return ERROR_MESSAGE_PREFIX + code.intValue();
        } else {
            return ERROR_MESSAGE_PREFIX + code.intValue() + " for " + path;
        }
    }

    /**
     * Error Code Definition
     */
    public enum Code {

        /**
         * Everything is ok. (You shouldn't use it).
         *
         * @see TaskConfigNodeException#create
         */
        OK(0),

        /**
         * System not definition error.
         */
        SYSTEM_ERROR(-1),

        /**
         * Node is null;
         */
        NODE_NULL(-2),

        /**
         * NodeType is null.
         */
        NODE_TYPE_NULL(-3);

        private final int code;

        Code(int code) {
            this.code = code;
        }

        public int intValue() {
            return code;
        }
    }

    /**
     * We hope that all {@link TaskConfigNodeException} should be created from here.
     *
     * @param code {@link Code}
     * @param path zookeeper register path.
     * @return {@link TaskConfigNodeException}
     */
    public static TaskConfigNodeException create(Code code, String path) {
        TaskConfigNodeException exception = create(code);
        exception.path = path;
        return exception;
    }

    /**
     * We hope that all {@link TaskConfigNodeException} should be created from here.
     *
     * @param code {@link Code}
     * @return {@link TaskConfigNodeException}
     */
    public static TaskConfigNodeException create(Code code) {
        switch (code) {
            case SYSTEM_ERROR:
                return new SystemErrorException();
            case NODE_NULL:
                return new NodeNullException();
            case NODE_TYPE_NULL:
                return new NodeTypeNullException();
            case OK:
            default:
                throw new IllegalArgumentException("Invalid exception code.");
        }
    }

    /**
     * @see Code#SYSTEM_ERROR
     */
    public static class SystemErrorException extends TaskConfigNodeException {
        public SystemErrorException() {
            super(Code.SYSTEM_ERROR);
        }
    }

    /**
     * @see Code#NODE_NULL
     */
    public static class NodeNullException extends TaskConfigNodeException {
        public NodeNullException() {
            super(Code.NODE_NULL);
        }
    }

    /**
     * @see Code#NODE_TYPE_NULL
     */
    public static class NodeTypeNullException extends TaskConfigNodeException {
        public NodeTypeNullException() {
            super(Code.NODE_TYPE_NULL);
        }
    }
}
