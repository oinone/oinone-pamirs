package pro.shushi.pamirs.middleware.schedule.core.util;

import org.springframework.scheduling.support.CronExpression;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TimeUnit;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Adamancy Zhang
 * @date 2020-11-11 15:17
 */
public class DateHelper {

    public static Date computeNextExecuteTime(Date date, TimeUnit timeUnit, Integer value) {
        if (timeUnit == null || value == null || value <= 0) {
            if (date == null) {
                return new Date();
            } else {
                return date;
            }
        }
        Date now = new Date();
        while (date.before(now)) {
            date = addDate(date, timeUnit, value);
        }
        return date;
    }

    public static Date addDate(Date date, TimeUnit timeUnit, Integer value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date == null ? new Date() : date);
        //noinspection MagicConstant
        calendar.add(timeUnit.intValue(), value);
        return calendar.getTime();
    }

    public static Date computeNextExecuteTime(Date date, String cron) {
        CronExpression cronExpression = CronExpression.parse(cron);
        LocalDateTime nextDate = cronExpression.next(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
        LocalDateTime now = LocalDateTime.now();
        while (nextDate.isBefore(now)) {
            nextDate = cronExpression.next(nextDate);
        }
        return Date.from(nextDate.atZone(ZoneId.systemDefault()).toInstant());
    }
}
