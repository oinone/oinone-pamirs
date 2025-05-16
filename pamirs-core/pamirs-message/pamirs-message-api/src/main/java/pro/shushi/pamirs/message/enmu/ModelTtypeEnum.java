package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mail.enmu.ModelTtypeEnum", displayName = "")
public enum ModelTtypeEnum implements IEnum<String> {

    ID( "ID", "ID","ID"),
    NULL( "NULL", "空指针","空指针"),
    INT( "int", "整数","整数"),
    LONG( "long", "长整数","长整数"),
    FLOAT( "float", "浮点数","浮点数"),
    BOOLEAN( "bool", "BOOL值","BOOL值"),
    STRING( "string", "字符串","字符串"),
    TEXT( "text", "文本行","文本行"),
    HTML( "html", "富文本","富文本"),
    ENUM( "enum", "枚举值","枚举值"),
    MULTI_ENUM( "multi_enum", "多选枚举值","多选枚举值"),
    DATE( "date", "日期时间","日期时间"),
    MONEY( "money", "金额","金额"),
    one2one( "o2o", "一对一","一对一"),
    many2one( "m2o", "多对一","多对一"),
    one2many( "o2m", "一对多","一对多"),
    many2many( "m2m", "多对多","多对多");

    private String help;

    private String value;

    private String displayName;

    ModelTtypeEnum(String value, String displayName,String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


    public String getHelp() {
        return help;
    }


    public String getValue() {
        return value;
    }


    public String getDisplayName() {
        return displayName;
    }
}
