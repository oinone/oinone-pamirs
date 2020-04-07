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

    MYSQL("MYSQL", "MYSQL数据源", "MYSQL数据源"),
    ElasticSearch("ElasticSearch", "ElasticSearch搜索引擎", "ElasticSearch搜索引擎");

    private String value;

    private String displayName;

    private String help;

    DataSourceEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
    }

}
