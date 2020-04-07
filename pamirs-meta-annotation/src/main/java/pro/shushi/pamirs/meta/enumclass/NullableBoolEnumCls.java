package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 布尔枚举（允许为null）
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.NullableBool", displayName = "可空布尔类型")
public class NullableBoolEnumCls extends BaseEnum<Boolean> {

    protected NullableBoolEnumCls(){}

    public static NullableBoolEnumCls[] values(){
        return BaseEnum.values();
    }

    public static NullableBoolEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static NullableBoolEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static NullableBoolEnumCls NULL       = of(NullableBoolEnumCls.class).init(NullableBoolEnum.NULL);
    public final static NullableBoolEnumCls TRUE       = of(NullableBoolEnumCls.class).init(NullableBoolEnum.TRUE);
    public final static NullableBoolEnumCls FALSE      = of(NullableBoolEnumCls.class).init(NullableBoolEnum.FALSE);

}
