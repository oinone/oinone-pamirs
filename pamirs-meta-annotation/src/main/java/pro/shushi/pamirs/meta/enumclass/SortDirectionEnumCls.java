package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

/**
 * 排序枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.SortDirection", displayName = "排序类型")
public class SortDirectionEnumCls extends BaseEnum<String> {

    protected SortDirectionEnumCls(){}

    public static SortDirectionEnumCls[] values(){
        return BaseEnum.values();
    }

    public static SortDirectionEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static SortDirectionEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static SortDirectionEnumCls ASC     = of(SortDirectionEnumCls.class).init(SortDirectionEnum.ASC);
    public final static SortDirectionEnumCls DESC    = of(SortDirectionEnumCls.class).init(SortDirectionEnum.DESC);

}
