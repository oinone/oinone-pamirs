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

    DATETIME("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"),
    TIMESTAMP("long", "时间戳", "整数时间戳"),
    DATE("yyyy-MM-dd", "yyyy-MM-dd", "yyyy-MM-dd"),
    TIME("HH:mm:ss", "HH:mm:ss", "HH:mm:ss"),
    YEAR("yyyy", "yyyy", "yyyy"),
    //Sat Dec 12 00:00:00 CST 2022
    DATETIME_EEE_ZZ("EEE MMM dd HH:mm:ss zzz yyyy", "EEE MMM dd HH:mm:ss zzz yyyy", "EEE MMM dd HH:mm:ss zzz yyyy"),
    //Tue Aug 21 2018 00:00:00 GMT+0800 (中国标准时间) 00:00:00
    DATETIME_E_MM_Z("E MMM dd yyyy HH:mm:ss z", "EEE MMM dd HH:mm:ss zzz yyyy", "EEE MMM dd HH:mm:ss zzz yyyy"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    DateFormatEnum(String value, String displayName, String help) {
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
