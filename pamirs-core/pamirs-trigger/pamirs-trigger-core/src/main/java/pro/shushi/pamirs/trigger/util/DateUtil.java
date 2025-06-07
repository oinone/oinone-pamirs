package pro.shushi.pamirs.trigger.util;

import pro.shushi.pamirs.core.common.DateHelper;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;

import java.util.Date;

public class DateUtil {

    public static Date computeNextExecuteTime(Date date, TimeUnitEnum timeUnit, Integer value) {
        if (timeUnit == null || value == null || value <= 0) {
            if (date == null) {
                return new Date();
            } else {
                return date;
            }
        }
        Date now = new Date();
        while (date.before(now) || date.equals(now)) {
            date = DateHelper.addValue(date, timeUnit, value);
        }
        return date;
    }

    public static void main(String[] args) {
        computeNextExecuteTime(new Date(), TimeUnitEnum.SECOND, 90);
    }

}
