package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 基本类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.Type", displayName = "基本类型")
public enum TypeEnum implements IEnum<String> {

    // 基本类型
    INTEGER("integer", "整数", "短整数（10位有效数字）和长整数（19位有效数字）"),
    FLOAT("float", "浮点数", "单精度浮点数（7-8位有效数字）和双精度浮点数（15-16位有效数字）"),
    BOOLEAN("bool", "布尔", "布尔，值为真或假"),
    STRING("string", "字符", "字符串"),
    DATETIME("datetime", "日期时间", "日期时间"),

    // 复杂类型
    ENUM("enum", "枚举", "枚举"),
    MODEL("model", "对象", "对象"),

    ;

    private String value;

    private String displayName;

    private String help;

    TypeEnum(String  value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help();
    }

}