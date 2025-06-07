package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

@Base
@Dict(dictionary = ParamTypeEnum.dictionary, displayName = "参数类型", summary = "用于指定参数转换时的处理方式")
public enum ParamTypeEnum implements IEnum<String> {

    OBJECT("OBJECT", "Object", "对象"),
    ENUMERATION("ENUMERATION", "枚举", "枚举"),
//    MULTI_ENUMERATION("MULTI_ENUMERATION", "多选枚举", "多选枚举")

    Long("Long", "Long", "Long 类型，字段长度可编辑"),
    Double("Double", "Double", "Double 类型，字段长度可编辑"),
    String("String", "String", "String 类型，字段长度可编辑"),
    Boolean("Boolean", "Boolean", "Boolean 类型，字段长度为 1"),
    Integer("Integer", "Integer", "Integer 类型，字段长度可编辑"),
    Date("Date", "Date", "Date 类型，字段长度不可编辑"),
    Void("Void", "Void", "Void 类型，字段长度不可编辑"),
    File("File", "File", "File 类型，字段长度不可编辑，本质是字符串，参数转换时会转化成二进制流"),

    ;

    public static final String dictionary = "pamirs.eip.EipParamTypeEnum";

    private String value;

    private String displayName;

    private String help;

    ParamTypeEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    public static ParamTypeEnum fetchByTtype(String ttypeValue) {
        if (TtypeEnum.isDateType(ttypeValue)) {
            return ParamTypeEnum.Date;
        } else if (TtypeEnum.isNumericType(ttypeValue)) {
            if (TtypeEnum.FLOAT.value().equals(ttypeValue)) {
                return ParamTypeEnum.Double;
            } else {
                return ParamTypeEnum.Integer;
            }
        } else if (TtypeEnum.isBoolType(ttypeValue)) {
            return ParamTypeEnum.Boolean;
        } else if (TtypeEnum.isStringType(ttypeValue)) {
            return ParamTypeEnum.String;
        } else if (TtypeEnum.ENUM.value().equals(ttypeValue)) {
            return ParamTypeEnum.String;
        } else if (TtypeEnum.isRelationType(ttypeValue) || TtypeEnum.MAP.value().equals(ttypeValue) || TtypeEnum.OBJ.value().equals(ttypeValue)) {
            return ParamTypeEnum.OBJECT;
        } else if (TtypeEnum.VOID.value().equals(ttypeValue)) {
            return ParamTypeEnum.Void;
        } else {
            return null;
        }
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHelp() {
        return help;
    }
}