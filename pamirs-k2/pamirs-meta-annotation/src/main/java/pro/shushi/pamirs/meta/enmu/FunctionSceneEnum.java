package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;

/**
 * 函数场景枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionScene", displayName = "函数场景")
public enum FunctionSceneEnum implements BitEnum {

    CONSTRAINT(2, "模型约束", "模型约束函数"),
    SERIALIZE(2 << 1, "模型序列化", "模型序列化函数"),
    SEQUENCE(2 << 2, "模型序列生成器", "模型序列生成器函数"),

    RSQL(2 << 6, "模型查询", "模型查询表达式"),
    EXPRESSION(2 << 7, "模型表达式", "模型计算表达式"),
    BUSINESS_SHOP(2 << 8, "模型表达式", "模型计算表达式"),
    BUSINESS_CORP(2 << 9, "模型表达式", "模型计算表达式"),

    ;

    private final Long value;

    private final String displayName;

    private final String help;

    FunctionSceneEnum(long value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public Long value() {
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
