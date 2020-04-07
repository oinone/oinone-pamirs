package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * 函数类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionType", displayName = "函数类型")
public class FunctionTypeEnumCls extends BaseEnum<String> {

    protected FunctionTypeEnumCls(){}

    public static FunctionTypeEnumCls[] values(){
        return BaseEnum.values();
    }

    public static FunctionTypeEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static FunctionTypeEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static FunctionTypeEnumCls JAVA         = of(FunctionTypeEnumCls.class).init(FunctionTypeEnum.JAVA);
    public final static FunctionTypeEnumCls DSL          = of(FunctionTypeEnumCls.class).init(FunctionTypeEnum.DSL);
    public final static FunctionTypeEnumCls JS           = of(FunctionTypeEnumCls.class).init(FunctionTypeEnum.JS);
    public final static FunctionTypeEnumCls MVEL         = of(FunctionTypeEnumCls.class).init(FunctionTypeEnum.MVEL);
    public final static FunctionTypeEnumCls SCRIPT       = of(FunctionTypeEnumCls.class).init(FunctionTypeEnum.EXPRESSION);
    public final static FunctionTypeEnumCls GROOVY       = of(FunctionTypeEnumCls.class).init(FunctionTypeEnum.GROOVY);

}
