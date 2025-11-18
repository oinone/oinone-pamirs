package pro.shushi.pamirs.grouping.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 分组统计工具类
 *
 * @author Gesi at 9:34 on 2025/9/25
 */
public class GroupStatisticUtils {

    public static Object formatNumber(Object number, int scale) {
        if (!(number instanceof Number)) {
            return number;
        }
        if (number instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) number;
            if (bigDecimal.stripTrailingZeros().scale() > 0) {
                return bigDecimal.setScale(scale, RoundingMode.HALF_UP);
            }
        } else if (number instanceof Float) {
            float scalePow = (float) Math.pow(10, scale);
            return Math.round(((float) number) * scalePow) / scalePow;
        } else if (number instanceof Double) {
            double scalePow = Math.pow(10, scale);
            return Math.round(((double) number) * scalePow) / scalePow;
        }
        return number;
    }

    public static Pair<Date, Date> earliestTimeAndLatestTime(List<?> dataList) {
        dataList = dataList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dataList)) {
            return Pair.of(null, null);
        }
        dataList = dataList.stream().sorted().collect(Collectors.toList());
        Object earliestTime = dataList.get(0);
        Object latestTime = dataList.get(dataList.size() - 1);
        if (earliestTime instanceof Number || earliestTime instanceof String) {
            earliestTime = new Date(Long.parseLong(earliestTime.toString()));
        } else {
            earliestTime = new Date(((Date) earliestTime).getTime());
        }
        if (latestTime instanceof Number || latestTime instanceof String) {
            latestTime = new Date(Long.parseLong(latestTime.toString()));
        } else {
            latestTime = new Date(((Date) latestTime).getTime());
        }
        return Pair.of((Date) earliestTime, (Date) latestTime);
    }

    public static Long timeRangeDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return null;
        }
        long diffInMillie = d1.getTime() - d2.getTime();
        return TimeUnit.DAYS.convert(Math.abs(diffInMillie), TimeUnit.MILLISECONDS);
    }

    public static Long timeRangeMonth(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return null;
        }
        LocalDate localDate1 = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return Math.abs(ChronoUnit.MONTHS.between(localDate2, localDate1));
    }

    public static Long timeRangeYear(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return null;
        }
        LocalDate localDate1 = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return Math.abs(ChronoUnit.YEARS.between(localDate2, localDate1));
    }

}
