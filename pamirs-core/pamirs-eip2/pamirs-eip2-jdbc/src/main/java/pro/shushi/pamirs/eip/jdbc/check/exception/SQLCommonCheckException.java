package pro.shushi.pamirs.eip.jdbc.check.exception;

import pro.shushi.pamirs.locale.utils.I18nUtils;

/**
 * SQL 公共检查异常
 *
 * @author Adamancy Zhang at 14:20 on 2024-06-06
 */
public class SQLCommonCheckException {

    public static SQLCheckException createNotAllowOperationTableException(String tableName) {
        return SQLCheckException.createException("C000001", I18nUtils.getMessage("pamirs.eip.jdbc.check.tableOperationNotAllowed", tableName));
    }

    public static SQLCheckException createTableNameIsNullException() {
        return SQLCheckException.createException("C000002", I18nUtils.getMessage("pamirs.eip.jdbc.check.tableNameParseFailed"));
    }
}
