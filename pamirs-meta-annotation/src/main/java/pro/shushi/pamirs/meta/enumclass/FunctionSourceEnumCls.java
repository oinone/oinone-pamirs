package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.FunctionSourceEnum;

/**
 * 函数来源枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionSource", displayName = "函数来源")
public class FunctionSourceEnumCls extends BaseEnum<String> {

    protected FunctionSourceEnumCls(){}

    public static FunctionSourceEnumCls[] values(){
        return BaseEnum.values();
    }

    public static FunctionSourceEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static FunctionSourceEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static FunctionSourceEnumCls FUNCTION         = of(FunctionSourceEnumCls.class).init(FunctionSourceEnum.FUNCTION);
    public final static FunctionSourceEnumCls DATACONFIG       = of(FunctionSourceEnumCls.class).init(FunctionSourceEnum.DATACONFIG);
    public final static FunctionSourceEnumCls ACTION           = of(FunctionSourceEnumCls.class).init(FunctionSourceEnum.ACTION);
    public final static FunctionSourceEnumCls EXTPOINT         = of(FunctionSourceEnumCls.class).init(FunctionSourceEnum.EXTPOINT);
    public final static FunctionSourceEnumCls HOOK             = of(FunctionSourceEnumCls.class).init(FunctionSourceEnum.HOOK);

}
