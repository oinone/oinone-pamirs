package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;

/**
 * 激活状态枚举类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.Active", displayName = "激活状态")
public class ActiveEnumCls extends BaseEnum<Boolean> {

    protected ActiveEnumCls(){}

    public static ActiveEnumCls[] values(){
        return BaseEnum.values();
    }

    public static ActiveEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static ActiveEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static ActiveEnumCls ACTIVE        = of(ActiveEnumCls.class).init(ActiveEnum.ACTIVE);
    public final static ActiveEnumCls INACTIVE      = of(ActiveEnumCls.class).init(ActiveEnum.INACTIVE);

}
