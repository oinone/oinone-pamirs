package pro.shushi.pamirs.framework.connectors.data.dialect.mysql.constants;

/**
 * 表配置默认项
 * <p>
 * 2023/06/25
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 */
public interface MySQLTableInfoConstants {

    boolean DEFAULT_LOGIC_DELETE = true;

    String DEFAULT_LOGIC_DELETE_COLUMN = "is_deleted";

    String DEFAULT_LOGIC_DELETE_VALUE = "REPLACE(unix_timestamp(NOW(6)),'.','')";

    String DEFAULT_LOGIC_NOT_DELETE_VALUE = "0";

    String DEFAULT_KEY_GENERATOR_VALUE = "DISTRIBUTION";

    boolean DEFAULT_UNDER_CAMEL = true;

    boolean DEFAULT_TABLE_NAME_CASE_SENSITIVE = false;

    boolean DEFAULT_CAPITAL_MODE = false;

    String DEFAULT_TABLE_FORMAT = "`%s`";

    String DEFAULT_COLUMN_FORMAT = "`%s`";

    String DEFAULT_ALIAS_FORMAT = "`%s`";

    String DEFAULT_CHARSET = "utf8mb4";

    String DEFAULT_COLLATE = "bin";

}
