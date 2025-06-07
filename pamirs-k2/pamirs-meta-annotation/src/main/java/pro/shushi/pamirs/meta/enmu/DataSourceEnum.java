package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 数据源枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.DataSource", displayName = "数据源类型")
public enum DataSourceEnum implements IEnum<String> {

    MYSQL("MySQL", "MYSQL数据源", "MYSQL数据源"),
    ORACLE("Oracle", "ORACLE数据源", "ORACLE数据源"),
    SQL_SERVER("MSSQL", "SQL Server数据源", "SQL Server数据源"),
    DM("DM", "DM数据源", "DM数据源"),
    KDB("KDB", "KDB数据源", "KDB数据源"),
    POSTGRE_SQL("PostgreSQL", "PostgreSQL数据源", "PostgreSQL数据源"),
    GAUSS_DB("GaussDB", "GaussDB数据源", "GaussDB数据源"),
    ElasticSearch("ElasticSearch", "ElasticSearch搜索引擎", "ElasticSearch搜索引擎");

    private final String value;

    private final String displayName;

    private final String help;

    DataSourceEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
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

}
