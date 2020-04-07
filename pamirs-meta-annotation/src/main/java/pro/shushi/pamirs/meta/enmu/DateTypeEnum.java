package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 时间类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.DateType", displayName = "时间类型")
public enum DateTypeEnum implements IEnum<String> {

    DATETIME("datetime", "日期时间", "日期时间"),
    //    YEAR("year", "年份", "年份"),
    DATE("date", "日期", "日期"),
    TIME("time", "时间", "时间"),
    ;

    private String value;

    private String displayName;

    private String help;

    DateTypeEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
