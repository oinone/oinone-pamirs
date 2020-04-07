package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.util.Calendar;

@Base
@Dict(dictionary = "base.DateUnit", displayName = "时间单位")
public enum DateUnitEnum implements IEnum<Integer> {

    SECOND(Calendar.SECOND, "秒", "秒"),
    MINUTE(Calendar.MINUTE, "分", "分"),
    HOUR(Calendar.HOUR, "小时", "小时"),
    DAY(Calendar.DAY_OF_YEAR, "天", "天"),
    WEEK(Calendar.WEEK_OF_YEAR, "周", "周"),
    MONTH(Calendar.MONTH, "月", "月"),
    YEAR(Calendar.YEAR, "年", "年");

    private Integer value;

    private String displayName;

    private String help;

    DateUnitEnum(Integer value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
