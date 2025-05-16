package pro.shushi.pamirs.eip.jdbc.check.exception;

/**
 * SQL Update检查异常
 *
 * @author Adamancy Zhang at 14:19 on 2024-06-06
 */
public class SQLUpdateCheckException {

    public static SQLCheckException createWhereIsNullException() {
        return SQLCheckException.createException("U000001", "更新语句必须使用Where条件");
    }

}
