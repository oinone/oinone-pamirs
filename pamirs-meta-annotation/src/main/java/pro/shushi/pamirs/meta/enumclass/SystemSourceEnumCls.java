package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * 系统来源枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.SystemSource", displayName = "系统来源")
public class SystemSourceEnumCls extends BaseEnum<String> {

    protected SystemSourceEnumCls(){}

    public static SystemSourceEnumCls[] values(){
        return BaseEnum.values();
    }

    public static SystemSourceEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static SystemSourceEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static SystemSourceEnumCls BASE     = of(SystemSourceEnumCls.class).init(SystemSourceEnum.BASE);
    public final static SystemSourceEnumCls MANUAL   = of(SystemSourceEnumCls.class).init(SystemSourceEnum.MANUAL);

}
