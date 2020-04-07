package pro.shushi.pamirs.meta.enumclass;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.FunctionSceneEnum;

/**
 * 函数场景枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionScene", displayName = "函数场景")
public class FunctionSceneEnumCls extends BaseEnum<String> {

    protected FunctionSceneEnumCls(){}

    public static FunctionSceneEnumCls[] values(){
        return BaseEnum.values();
    }

    public static FunctionSceneEnumCls valueOf(String name){
        return BaseEnum.valueOf(name);
    }

    public static FunctionSceneEnumCls valueFor(String value){
        return BaseEnum.valueFor(value);
    }

    public final static FunctionSceneEnumCls CONSTRAINT                 = of(FunctionSceneEnumCls.class).init(FunctionSceneEnum.CONSTRAINT);
    public final static FunctionSceneEnumCls SERIALIZE                  = of(FunctionSceneEnumCls.class).init(FunctionSceneEnum.SERIALIZE);
    public final static FunctionSceneEnumCls SEQUENCE                   = of(FunctionSceneEnumCls.class).init(FunctionSceneEnum.SEQUENCE);

    public final static FunctionSceneEnumCls RSQL                       = of(FunctionSceneEnumCls.class).init(FunctionSceneEnum.RSQL);

    public final static FunctionSceneEnumCls EXPRESSION                 = of(FunctionSceneEnumCls.class).init(FunctionSceneEnum.EXPRESSION);

    public final static FunctionSceneEnumCls LOGIC_FOREACH              = of(FunctionSceneEnumCls.class).init(FunctionSceneEnum.LOGIC_FOREACH);
    public final static FunctionSceneEnumCls LOGIC_IF                   = of(FunctionSceneEnumCls.class).init(FunctionSceneEnum.LOGIC_IF);
    public final static FunctionSceneEnumCls LOGIC_GOTO                 = of(FunctionSceneEnumCls.class).init(FunctionSceneEnum.LOGIC_GOTO);
    public final static FunctionSceneEnumCls LOGIC_QUERY_AGGREGATION    = of(FunctionSceneEnumCls.class).init(FunctionSceneEnum.LOGIC_QUERY_AGGREGATION);
    public final static FunctionSceneEnumCls LOGIC_QUERY_PAGE           = of(FunctionSceneEnumCls.class).init(FunctionSceneEnum.LOGIC_QUERY_PAGE);

    public final static FunctionSceneEnumCls PROCESS_DECISION           = of(FunctionSceneEnumCls.class).init(FunctionSceneEnum.PROCESS_DECISION);
    public final static FunctionSceneEnumCls PROCESS_TO                 = of(FunctionSceneEnumCls.class).init(FunctionSceneEnum.PROCESS_TO);

}
