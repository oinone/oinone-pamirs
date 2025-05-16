package pro.shushi.pamirs.file.api.exception;

/**
 * Excel模板异常
 *
 * @author Adamancy Zhang at 21:11 on 2024-03-28
 */
public class ExcelTemplateException extends RuntimeException {

    private static final long serialVersionUID = 7345528567506349479L;

    public ExcelTemplateException(String message) {
        super(message);
    }
}
