package pro.shushi.pamirs.eip.jdbc.check.exception;

/**
 * SQL 解析异常
 *
 * @author Adamancy Zhang at 10:28 on 2024-06-06
 */
public class SQLParseCheckException {

    public static SQLCheckException createSQLError(Throwable cause) {
        return SQLCheckException.createException("P000001", "无法解析的SQL", cause);
    }

    public static SQLCheckException createSQLVisitError(Throwable cause) {
        return SQLCheckException.createException("P000002", "无法正确处理的SQL", cause);
    }

    public static SQLCheckException createSingleSQLException() {
        return SQLCheckException.createException("P000003", "仅允许使用一条SQL语句");
    }

}
