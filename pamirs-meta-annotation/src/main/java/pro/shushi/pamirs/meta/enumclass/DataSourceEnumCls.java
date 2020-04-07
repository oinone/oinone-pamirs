package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.DataSourceEnum;

/**
 * 数据源枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.DataSource", displayName = "数据源类型")
public class DataSourceEnumCls extends BaseEnum<String> {

    protected DataSourceEnumCls(){}

    public static DataSourceEnumCls[] values(){
        return BaseEnum.values();
    }

    public static DataSourceEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static DataSourceEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static DataSourceEnumCls MYSQL             = of(DataSourceEnumCls.class).init(DataSourceEnum.MYSQL);
    public final static DataSourceEnumCls ElasticSearch     = of(DataSourceEnumCls.class).init(DataSourceEnum.ElasticSearch);

}
