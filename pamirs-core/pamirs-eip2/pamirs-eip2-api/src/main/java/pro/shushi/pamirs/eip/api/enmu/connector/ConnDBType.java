package pro.shushi.pamirs.eip.api.enmu.connector;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ConnDBType
 *
 * @author yakir on 2023/03/29 16:07.
 */
@Base
@Dict(dictionary = ConnDBType.dictionary, displayName = "DB类型", summary = "DB类型")
public enum ConnDBType implements IEnum<String> {

    MySQL("MySQL", "MySQL", "MySQL", "com.mysql.cj.jdbc.Driver"),
    SQLServer("SQLServer", "SQL Server", "SQL Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    Oracle("Oracle", "Oracle", "Oracle", "oracle.jdbc.OracleDriver"),
    PostgreSQL("PostgreSQL", "PostgreSQL", "PostgreSQL", "org.postgresql.Driver"),

    ;

    public static final String dictionary = "designer.ConnDBType";

    private final String value;
    private final String displayName;
    private final String help;
    private final String driver;

    ConnDBType(String value, String displayName, String help, String driver) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.driver = driver;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }

    public String getDriver() {
        return driver;
    }
}
