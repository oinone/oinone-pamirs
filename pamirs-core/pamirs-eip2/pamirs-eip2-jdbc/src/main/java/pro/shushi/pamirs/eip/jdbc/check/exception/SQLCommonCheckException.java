package pro.shushi.pamirs.eip.jdbc.check.exception;

/**
 * SQL 公共检查异常
 *
 * @author Adamancy Zhang at 14:20 on 2024-06-06
 */
public class SQLCommonCheckException {

    public static SQLCheckException createNotAllowOperationTableException(String tableName) {
        return SQLCheckException.createException("C000001", String.format("不允许操作%s数据表", tableName));
    }

    public static SQLCheckException createTableNameIsNullException() {
        return SQLCheckException.createException("C000002", "表名解析失败");
    }
}
