package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 函数场景枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionScene", displayName = "函数场景")
public enum FunctionSceneEnum implements IEnum<String> {

    CONSTRAINT("constraint", "模型约束场景-特殊约束函数", "模型约束-特殊约束函数"),
    SERIALIZE("SERIALIZE", "序列化场景", "模型-序列化函数"),
    SEQUENCE("SEQUENCE", "模型-序列生成器场景", "模型-序列生成器函数"),

    RSQL("RSQL", "模型查询场景", "模型查询场景"),

    EXPRESSION("EXPRESSION", "模型处理数据场景", "模型处理数据场景"),

    LOGIC_FOREACH("LOGIC_FOREACH", "逻辑-循环表达式", "逻辑-循环表达式"),
    LOGIC_IF("LOGIC_IF", "逻辑-条件表达式", "逻辑-条件表达式"),
    LOGIC_GOTO("LOGIC_GOTO", "逻辑-跳转表达式", "逻辑-跳转表达式"),
    LOGIC_QUERY_AGGREGATION("LOGIC_QUERY_AGGREGATION", "逻辑-查询-聚合条件", "逻辑-查询-聚合条件"),
    LOGIC_QUERY_PAGE("LOGIC_QUERY_PAGE", "逻辑-查询-分页条件", "逻辑-查询-分页条件"),

    PROCESS_DECISION("PROCESS_DECISION", "流程-分支表达式", "流程-分支表达式"),
    PROCESS_TO("PROCESS_TO", "流程-跳转表达式", "流程-跳转表达式"),

    ;

    private String value;

    private String displayName;

    private String help;

    FunctionSceneEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
