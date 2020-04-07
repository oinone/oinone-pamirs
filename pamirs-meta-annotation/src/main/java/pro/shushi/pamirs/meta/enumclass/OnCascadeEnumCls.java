package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.OnCascadeEnum;

/**
 * 关联操作枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.OnCascade", displayName = "关联操作")
public class OnCascadeEnumCls extends BaseEnum<String> {

    protected OnCascadeEnumCls(){}

    public static OnCascadeEnumCls[] values(){
        return BaseEnum.values();
    }

    public static OnCascadeEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static OnCascadeEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static OnCascadeEnumCls NO_ACTION     = of(OnCascadeEnumCls.class).init(OnCascadeEnum.NO_ACTION);
    public final static OnCascadeEnumCls SET_NULL      = of(OnCascadeEnumCls.class).init(OnCascadeEnum.SET_NULL);
    public final static OnCascadeEnumCls CASCADE       = of(OnCascadeEnumCls.class).init(OnCascadeEnum.CASCADE);
    public final static OnCascadeEnumCls RESTRICT      = of(OnCascadeEnumCls.class).init(OnCascadeEnum.RESTRICT);

}
