package pro.shushi.pamirs.file.api.exception;

/**
 * Wrapper check exception to runtime exception.
 *
 * @author Adamancy Zhang at 13:38 on 2024-04-01
 */
public class ExcelRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -614495882125689872L;

    public ExcelRuntimeException(Throwable cause) {
        super(cause);
    }
}
