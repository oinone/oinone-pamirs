package pro.shushi.pamirs.eip.api.constant;

import pro.shushi.pamirs.eip.api.model.connector.ConnDbType;

/**
 * @author Adamancy Zhang at 17:57 on 2025-08-13
 */
public class EipSystemDataSourceType {

    private EipSystemDataSourceType() {
        // reject create object
    }

    public static ConnDbType mysql() {
        ConnDbType type = new ConnDbType();
        final String code = "MySQL";
        type.setCode(code);
        type.setDisplayName(code);
        type.setHelp(code);
        type.setDriver("com.mysql.jdbc.Driver");
        type.setBasic(true);
        return type;
    }

    public static ConnDbType mssql() {
        ConnDbType type = new ConnDbType();
        final String code = "SQLServer";
        type.setCode(code);
        type.setDisplayName(code);
        type.setHelp(code);
        type.setDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        type.setBasic(true);
        return type;
    }

    public static ConnDbType oracle() {
        ConnDbType type = new ConnDbType();
        final String code = "Oracle";
        type.setCode(code);
        type.setDisplayName(code);
        type.setHelp(code);
        type.setDriver("oracle.jdbc.OracleDriver");
        type.setBasic(true);
        return type;
    }

    public static ConnDbType pgsql() {
        ConnDbType type = new ConnDbType();
        final String code = "PostgreSQL";
        type.setCode(code);
        type.setDisplayName(code);
        type.setHelp(code);
        type.setDriver("org.postgresql.Driver");
        type.setBasic(true);
        return type;
    }
}
