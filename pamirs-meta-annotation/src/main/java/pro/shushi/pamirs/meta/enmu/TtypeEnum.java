package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 字段类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.Ttype", displayName = "业务类型")
public enum TtypeEnum implements IEnum<String> {

    // 基本类型
    INTEGER("integer", "整数", "短整数（10位有效数字）和长整数（19位有效数字）"),
    FLOAT("float", "浮点数", "单精度浮点数（7-8位有效数字）和双精度浮点数（15-16位有效数字）"),
    BOOLEAN("bool", "布尔", "布尔，值为真或假"),
    STRING("string", "字符", "字符串"),
    TEXT("text", "文本", "多行文本"),
    DATETIME("datetime", "日期时间", "日期时间"),
    DATE("date", "日期", "日期"),
    TIME("time", "时间","时间"),

    // 复杂类型
    MONEY("money", "金额", "金额"),
    HTML("html", "富文本", "富文本"),
    RELATED("related", "引用", "引用类型"),
    ENUM("enum", "枚举", "枚举"),

    // 关系类型
    O2O("o2o", "一对一", "一对一"),
    O2M("o2m", "一对多", "一对多"),
    M2O("m2o", "多对一", "多对一"),
    M2M("m2m", "多对多", "多对多"),

    ;

    private String value;

    private String displayName;

    private String help;

    TtypeEnum(String  value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help();
    }

}