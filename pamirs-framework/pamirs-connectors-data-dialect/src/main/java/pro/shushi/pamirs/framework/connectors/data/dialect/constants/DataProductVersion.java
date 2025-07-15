package pro.shushi.pamirs.framework.connectors.data.dialect.constants;

import pro.shushi.pamirs.meta.enmu.DataSourceEnum;

/**
 * 数据存储版本号
 * <p>
 * 2020/7/31 4:15 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface DataProductVersion {

    String KEY_TYPE = "type";

    String KEY_VERSION = "version";

    String KEY_MAJOR_VERSION = "majorVersion";

    String DEFAULT_PRODUCT = DataSourceEnum.MYSQL.value();

    String PRODUCT_MYSQL = "MySQL";

    String DEFAULT_MYSQL_VERSION = "8.0";

    String DEFAULT_MYSQL_MAJOR_VERSION = "8";

    String DEFAULT_MYSQL_NDS_VERSION = "8.0-nds";

    String PRODUCT_ORACLE = "Oracle";

    String DEFAULT_ORACLE_VERSION = "12.2";

    String DEFAULT_ORACLE_MAJOR_VERSION = "12c";

    String ORACLE_11_VERSION = "11.2";

    String ORACLE_11_MAJOR_VERSION = "11g";

    String PRODUCT_DM = "DM";

    String DEFAULT_DM_VERSION = "8";

    String DEFAULT_DM_MAJOR_VERSION = "8";
//    String DEFAULT_DM_MAJOR_VERSION = "20230418";

    String PRODUCT_DM7 = "DM7";

    String DEFAULT_DM7_VERSION = "7";

    String DEFAULT_DM7_MAJOR_VERSION = "20220916";

    String PRODUCT_POSTGRE_SQL = "PostgreSQL";

    String DEFAULT_POSTGRE_SQL_VERSION = "14";

    String DEFAULT_POSTGRE_SQL_MAJOR_VERSION = "14.3";

    String PRODUCT_GAUSS_DB = "GaussDB";

    String DEFAULT_GAUSS_DB_VERSION = "5";

    String DEFAULT_GAUSS_DB_MAJOR_VERSION = "5.0.1";

    /**
     * @deprecated please using {@link DataProductVersion#PRODUCT_GAUSS_DB}
     */
    @Deprecated
    String PRODUCT_GAUSS = PRODUCT_GAUSS_DB;

    /**
     * @deprecated please using {@link DataProductVersion#DEFAULT_GAUSS_DB_VERSION}
     */
    @Deprecated
    String DEFAULT_GAUSS_VERSION = DEFAULT_GAUSS_DB_VERSION;

    /**
     * @deprecated please using {@link DataProductVersion#DEFAULT_GAUSS_DB_MAJOR_VERSION}
     */
    @Deprecated
    String DEFAULT_GAUSS_MAJOR_VERSION = DEFAULT_GAUSS_DB_MAJOR_VERSION;

    String PRODUCT_SQLSERVER = "MSSQL";

    String DEFAULT_SQLSERVER_VERSION = "2017";

    String DEFAULT_SQLSERVER_MAJOR_VERSION = "2017";

    String PRODUCT_KDB = "KDB";

    String DEFAULT_KDB_VERSION = "9";

    String DEFAULT_KDB_MAJOR_VERSION = "V009R001C001B0030";
}