package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum;

/**
 * 函数分类枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionCategory", displayName = "函数分类")
public class FunctionCategoryEnumCls extends BaseEnum<String> {

    protected FunctionCategoryEnumCls(){}

    public static FunctionCategoryEnumCls[] values(){
        return BaseEnum.values();
    }

    public static FunctionCategoryEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static FunctionCategoryEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static FunctionCategoryEnumCls MATH         = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.MATH);
    public final static FunctionCategoryEnumCls TEXT         = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.TEXT);
    public final static FunctionCategoryEnumCls TIME         = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.TIME);
    public final static FunctionCategoryEnumCls COLLECTION   = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.COLLECTION);
    public final static FunctionCategoryEnumCls LOGIC        = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.LOGIC);

    public final static FunctionCategoryEnumCls CONTEXT      = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.CONTEXT);
    public final static FunctionCategoryEnumCls OTHER        = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.OTHER);
    public final static FunctionCategoryEnumCls SQL_DML      = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.SQL_DML);
    public final static FunctionCategoryEnumCls SQL_DSL      = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.SQL_DSL);
    public final static FunctionCategoryEnumCls SQL_AGG      = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.SQL_AGG);

    public final static FunctionCategoryEnumCls RESULT_MAP   = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.RESULT_MAP);
    public final static FunctionCategoryEnumCls BUILTIN      = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.BUILTIN);
    public final static FunctionCategoryEnumCls CONSTRAINT   = of(FunctionCategoryEnumCls.class).init(FunctionCategoryEnum.CONSTRAINT);

}
