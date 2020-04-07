package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 函数分类枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionCategory", displayName = "函数分类")
public enum FunctionCategoryEnum implements IEnum<String> {

    MATH("MATH", "数学函数", "数学函数"),
    TEXT("TEXT", "文本函数", "文本函数"),
    TIME("TIME", "时间函数", "时间函数"),
    COLLECTION("COLLECTION", "集合函数", "集合函数"),
    LOGIC("LOGIC", "逻辑函数", "逻辑函数"),
    CONTEXT("CONTEXT", "上下文函数", "上下文函数"),
    OTHER("OTHER", "其他函数", "其他函数"),
    SQL_DML("DML", "数据处理函数", "数据处理函数"),
    SQL_DSL("DSL", "数据查询函数", "数据查询函数"),
    SQL_AGG("SQL_AGG", "SQL聚合函数", "SQL聚合函数"),
    RESULT_MAP("MAP", "返回结果构建函数", "返回结果构建函数"),
    BUILTIN("BUILTIN", "内置函数", "内置函数"),
    CONSTRAINT("CONSTRAINT", "模型约束函数", "模型约束函数"),
    ;

    private String value;

    private String displayName;

    private String help;

    FunctionCategoryEnum(String  value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
