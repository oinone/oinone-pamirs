package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;

/**
 * 字段类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.Ttype", displayName = "业务类型")
public final class TtypeEnum extends RtypeEnum {

    private static final long serialVersionUID = -4102685928482961816L;

    // 基本类型
    public final static TtypeEnum BINARY = create("BINARY", "binary", "二进制", "二进制");
    public final static TtypeEnum INTEGER = create("INTEGER", "integer", "整数", "短整数（10位有效数字）和长整数（19位有效数字）");
    public final static TtypeEnum FLOAT = create("FLOAT", "float", "浮点数", "单精度浮点数（7位有效数字）和双精度浮点数（15位有效数字）");
    public final static TtypeEnum BOOLEAN = create("BOOLEAN", "bool", "布尔", "布尔，值为真或假");
    public final static TtypeEnum STRING = create("STRING", "string", "文本", "字符串");
    public final static TtypeEnum TEXT = create("TEXT", "text", "多行文本", "多行文本");
    public final static TtypeEnum HTML = create("HTML", "html", "富文本", "富文本");
    public final static TtypeEnum ENUM = create("ENUM", "enum", "枚举", "枚举");
    public final static TtypeEnum DATETIME = create("DATETIME", "datetime", "日期时间", "日期时间");
    public final static TtypeEnum YEAR = create("YEAR", "year", "年份", "年份");
    public final static TtypeEnum DATE = create("DATE", "date", "日期", "日期");
    public final static TtypeEnum TIME = create("TIME", "time", "时间", "时间");
    public final static TtypeEnum UID = create("UID", "uid", "用户ID", "用户ID");
    public final static TtypeEnum PHONE = create("PHONE", "phone", "手机号", "手机号");
    public final static TtypeEnum EMAIL = create("EMAIL", "email", "邮箱", "邮箱");

    // 复合类型
    public final static TtypeEnum MONEY = create("MONEY", "money", "金额", "金额");
    public final static TtypeEnum MAP = create("MAP", "map", "键值对", "键值对");
    public final static TtypeEnum OBJ = create("OBJ", "obj", "泛型", "泛型");
    public final static TtypeEnum VOID = create("VOID", "void", "无", "无");

    // 引用类型
    public final static TtypeEnum RELATED = create("RELATED", "related", "引用", "引用类型");

    // 关系类型
    public final static TtypeEnum O2O = ref(RtypeEnum.O2O);
    public final static TtypeEnum O2M = ref(RtypeEnum.O2M);
    public final static TtypeEnum M2O = ref(RtypeEnum.M2O);
    public final static TtypeEnum M2M = ref(RtypeEnum.M2M);

    public static boolean isNumericType(String ttype) {
        return BaseEnum.isIn(ttype, caseValue(), BINARY, INTEGER, FLOAT, UID);
    }

    public static boolean isStringType(String ttype) {
        return BaseEnum.isIn(ttype, caseValue(), STRING, TEXT, HTML, PHONE, EMAIL);
    }

    public static boolean isDateType(String ttype) {
        return BaseEnum.isIn(ttype, caseValue(), DATETIME, YEAR, DATE, TIME);
    }

    public static boolean isBoolType(String ttype) {
        return BOOLEAN.value().equals(ttype);
    }

    public static boolean isBasicType(String ttype) {
        return isNumericType(ttype) || isStringType(ttype) || isDateType(ttype) || isBoolType(ttype);
    }

    public static boolean isRelatedType(String ttype) {
        return RELATED.value().equals(ttype);
    }

}

