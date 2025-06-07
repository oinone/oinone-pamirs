package pro.shushi.pamirs.eip.jdbc.check.exception;

/**
 * SQL Select检查异常
 *
 * @author Adamancy Zhang at 09:58 on 2024-06-06
 */
public class SQLSelectCheckException {

    public static SQLCheckException createWhereIsNullException() {
        return SQLCheckException.createException("S000001", "查询语句必须使用Where条件");
    }

}
