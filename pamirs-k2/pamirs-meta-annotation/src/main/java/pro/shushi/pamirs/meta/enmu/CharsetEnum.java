package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 字符集枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.Charset", displayName = "字符集")
public enum CharsetEnum implements IEnum<String> {

    DEFAULT("default", "DEFAULT", "DEFAULT"),
    UTF8("utf8", "UTF8", "UTF8"),
    UTF8MB4("utf8mb4", "UTF8MB4", "UTF8MB4"),
    UTF16("utf16", "UTF16", "UTF16"),
    UTF32("utf32", "UTF32", "UTF32"),
    LATIN1("latin1", "LATIN1", "LATIN1"),
    GB18030("gb18030", "GB18030", "GB18030"),
    GB2312("gb2312", "GB2312", "GB2312"),
    GBK("gbk", "GBK", "GBK"),
    ASCII("ascii", "ASCII", "ASCII"),
    BIG5("big5", "BIG5", "BIG5"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    CharsetEnum(String value, String displayName, String help) {
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
