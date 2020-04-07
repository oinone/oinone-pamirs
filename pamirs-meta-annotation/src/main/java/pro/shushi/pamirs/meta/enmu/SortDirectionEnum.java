package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 排序枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.SortDirection", displayName = "排序类型")
public enum SortDirectionEnum implements IEnum<String> {

    ASC("ASC", "升序", "升序"),
    DESC("DESC", "降序", "降序"),
    ;

    private String value;

    private String displayName;

    private String help;

    SortDirectionEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }
}
