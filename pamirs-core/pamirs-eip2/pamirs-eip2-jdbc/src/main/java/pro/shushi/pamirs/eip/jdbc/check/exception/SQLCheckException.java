package pro.shushi.pamirs.eip.jdbc.check.exception;

/**
 * SQL check exception
 *
 * @author Adamancy Zhang at 15:35 on 2024-05-17
 */
public class SQLCheckException extends RuntimeException {

    private static final long serialVersionUID = 8790684911274198504L;

    private final String code;

    private final String msg;

    private SQLCheckException(String code, String msg) {
        super(formatMessage(code, msg, null));
        this.code = code;
        this.msg = msg;
    }

    private SQLCheckException(String code, String msg, Throwable cause) {
        super(formatMessage(code, msg, cause), cause);
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    private static String formatMessage(String code, String message, Throwable cause) {
        StringBuilder builder = new StringBuilder("Error code: ");
        builder.append(code)
                .append(", message: ")
                .append(message);
        if (cause != null) {
            builder.append(", cause: ")
                    .append(cause.getMessage());
        }
        return builder.toString();
    }

    public static SQLCheckException createException(String code, String message) {
        return new SQLCheckException(code, message);
    }

    public static SQLCheckException createException(String code, String message, Throwable cause) {
        return new SQLCheckException(code, message, cause);
    }

    public static SQLCheckException createCommonException() {
        return new SQLCheckException("Oops!", "SQL check error");
    }

    public static SQLCheckException createCommonException(Throwable cause) {
        return new SQLCheckException("Oops!", "SQL check error", cause);
    }
}
