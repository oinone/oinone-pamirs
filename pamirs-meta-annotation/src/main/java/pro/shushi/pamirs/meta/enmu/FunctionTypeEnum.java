package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 函数类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FunctionType", displayName = "函数类型")
public enum FunctionTypeEnum implements IEnum<String> {

    JAVA("JAVA", "JAVA", "java代码"),
    DSL("DSL", "DSL", "自定义代码"),
    JS("JS", "JS", "JS代码"),
    MVEL("MVEL", "MVEL", "动态表达式"),
    EXPRESSION("EXPRESSION", "EXPRESSION", "表达式"),
    GROOVY("GROOVY", "GROOVY", "groovy代码");

    private String value;

    private String displayName;

    private String help;

    FunctionTypeEnum(String  value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
