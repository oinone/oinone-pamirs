package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 转换方式枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.Serialize", displayName = "序列化方式")
public enum SerializeEnum implements IEnum<String> {

    JSON("JSON", "JSON序列化", "JSON序列化"),
    COMMA("COMMA", "逗号拼接集合元素", "逗号拼接集合元素"),
    DOT("DOT", "点拼接集合元素", "点拼接集合元素")
    ;

    private String value;

    private String displayName;

    private String help;

    SerializeEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
