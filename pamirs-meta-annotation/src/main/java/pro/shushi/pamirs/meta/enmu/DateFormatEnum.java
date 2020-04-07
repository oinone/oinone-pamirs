package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 时间格式枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.DateFormat", displayName = "时间格式")
public enum DateFormatEnum implements IEnum<String> {

    DATETIME("datetime","yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"),
    DATE("date", "yyyy-MM-dd", "yyyy-MM-dd", "yyyy-MM-dd"),
    TIME("time", "HH:mm:ss", "HH:mm:ss", "HH:mm:ss"),
    YEAR("datetime", "yyyy", "yyyy", "yyyy"),
    ;

    private String value;

    private String displayName;

    private String help;

    private String type;

    DateFormatEnum(String type, String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
