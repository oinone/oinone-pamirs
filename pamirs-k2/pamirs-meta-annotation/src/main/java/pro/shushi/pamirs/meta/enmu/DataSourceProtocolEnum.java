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
@Dict(dictionary = "base.DataSourceProtocol", displayName = "数据源协议类型")
public enum DataSourceProtocolEnum implements IEnum<String> {

    MYSQL("jdbc:mysql", "MYSQL数据源", "MYSQL数据源"),
    ORACLE("jdbc:oracle", "ORACLE数据源", "ORACLE数据源"),
    SQL_SERVER("jdbc:sqlserver", "SQL Server数据源", "SQL Server数据源"),
    DM("jdbc:dm", "DM数据源", "DM数据源"),
    POSTGRE_SQL("jdbc:postgresql", "PostgreSQL数据源", "PostgreSQL数据源"),
    GAUSS_DB("jdbc:postgresql", "GaussDB数据源", "GaussDB数据源"),
    KDB8("jdbc:kingbase8", "Kingbase8", "Kingbase8"),
    NDS("jdbc:nds", "NDS数据源", "NDS数据源"),
    ElasticSearch("jdbc:es", "ElasticSearch搜索引擎", "ElasticSearch搜索引擎");

    private final String value;

    private final String displayName;

    private final String help;

    DataSourceProtocolEnum(String value, String displayName, String help) {
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
