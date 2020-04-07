package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.InheritedTypeEnum;

/**
 * 继承类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.InheritedType", displayName = "继承类型")
public class InheritedTypeEnumCls extends BaseEnum<String> {

    protected InheritedTypeEnumCls(){}

    public static InheritedTypeEnumCls[] values(){
        return BaseEnum.values();
    }

    public static InheritedTypeEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static InheritedTypeEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static InheritedTypeEnumCls ABSTRACT      = of(InheritedTypeEnumCls.class).init(InheritedTypeEnum.ABSTRACT);
    public final static InheritedTypeEnumCls EXTENDS      = of(InheritedTypeEnumCls.class).init(InheritedTypeEnum.EXTENDS);
    public final static InheritedTypeEnumCls MULTI         = of(InheritedTypeEnumCls.class).init(InheritedTypeEnum.MULTI);
    public final static InheritedTypeEnumCls PROXY         = of(InheritedTypeEnumCls.class).init(InheritedTypeEnum.PROXY);

}
