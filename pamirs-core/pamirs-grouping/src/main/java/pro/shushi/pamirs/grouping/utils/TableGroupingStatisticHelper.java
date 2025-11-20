package pro.shushi.pamirs.grouping.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 表格分组统计帮助类
 *
 * @author Gesi at 9:34 on 2025/9/25
 */
public class TableGroupingStatisticHelper {

    private TableGroupingStatisticHelper() {
        // reject create object
    }

    public static long timeRangeDay(Date d1, Date d2) {
        long diffInMillie = d1.getTime() - d2.getTime();
        return TimeUnit.DAYS.convert(Math.abs(diffInMillie), TimeUnit.MILLISECONDS);
    }

    public static long timeRangeMonth(Date d1, Date d2) {
        LocalDate localDate1 = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Math.abs(ChronoUnit.MONTHS.between(localDate2, localDate1));
    }

    public static long timeRangeYear(Date d1, Date d2) {
        LocalDate localDate1 = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Math.abs(ChronoUnit.YEARS.between(localDate2, localDate1));
    }
}
