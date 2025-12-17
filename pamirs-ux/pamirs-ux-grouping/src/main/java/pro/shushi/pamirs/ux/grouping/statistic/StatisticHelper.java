package pro.shushi.pamirs.ux.grouping.statistic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 统计帮助类
 *
 * @author Adamancy Zhang at 15:46 on 2025-11-20
 */
public class StatisticHelper {

    private StatisticHelper() {
        // reject create object
    }

    public static String computePercent(long count, long total) {
        if (total == 0) {
            return BigDecimal.ZERO.toPlainString();
        }
        return BigDecimal.valueOf(count)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100L))
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
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
