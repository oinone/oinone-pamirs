package pro.shushi.pamirs.middleware.schedule.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public class ScheduleDayWeek {

    public static int getDayWeek(LocalDateTime time) {
        return time.getDayOfWeek().getValue();
    }

    public static int getDayWeek() {
        return getDayWeek(LocalDateTime.now());
    }

    public static int getAmPm() {
        return LocalDateTime.now().get(ChronoField.AMPM_OF_DAY);
    }

}
