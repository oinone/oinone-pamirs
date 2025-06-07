package pro.shushi.pamirs.file.api.exception;

/**
 * 没有数据异常
 *
 * @author Adamancy Zhang at 21:06 on 2024-03-28
 */
public class NoDataException extends RuntimeException {

    private static final long serialVersionUID = -3286281967275594712L;

    public NoDataException(String message) {
        super(message);
    }
}
