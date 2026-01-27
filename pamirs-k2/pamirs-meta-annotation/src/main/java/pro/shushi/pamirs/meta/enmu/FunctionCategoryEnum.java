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
    REGEX("REGEX", "正则函数", "正则函数"),
    TIME("TIME", "时间函数", "时间函数"),
    COLLECTION("COLLECTION", "集合函数", "集合函数"),
    MAP("MAP", "键值对函数", "键值对函数"),
    OBJECT("OBJECT", "对象函数", "对象函数"),
    CONTEXT("CONTEXT", "上下文函数", "上下文函数"),
    LOGIC("LOGIC", "逻辑函数", "逻辑函数"),

    INSERT_ONE("INSERT_ONE", "新增单条记录", "新增单条记录"),
    INSERT_BATCH("INSERT_BATCH", "批量新增", "批量新增"),
    UPDATE_ONE("UPDATE_ONE", "更新单条记录", "更新单条记录"),
    UPDATE_BATCH("UPDATE_BATCH", "批量更新", "批量更新"),
    DELETE_ONE("DELETE_ONE", "删除单条记录", "删除单条记录"),
    DELETE_BATCH("DELETE_BATCH", "批量删除", "批量删除"),
    QUERY_ONE("QUERY_ONE", "查询单条记录", "查询单条记录"),
    QUERY_LIST("QUERY_LIST", "查询多条记录", "查询多条记录"),
    QUERY_PAGE("QUERY_PAGE", "分页查询", "分页查询"),

    CONSTRAINT("CONSTRAINT", "模型校验", "模型校验"),

    OTHER("OTHER", "其他函数", "其他函数"),
    CUSTOM_DESIGNER("CUSTOM_DESIGNER", "设计器自定义", "设计器自定义"),

    EIP_IN_OUT("EIP_IN_OUT", "输入输出转换器", "输入输出转换器"),

    AI_TOOL("AI_TOOL", "Ai工具", "Ai工具"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    FunctionCategoryEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }

}
