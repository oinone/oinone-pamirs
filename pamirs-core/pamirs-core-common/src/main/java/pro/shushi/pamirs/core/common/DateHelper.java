package pro.shushi.pamirs.core.common;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateHelper {

    public static final String DEFAULT_DATE_PATTERN = DateFormatEnum.DATETIME.value();

    @SuppressWarnings("MagicConstant")
    public static Date addValue(Date date, TimeUnitEnum timeUnit, Integer value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date == null ? new Date() : date);
        calendar.add(timeUnit.getCalendarValue(), value);
        return calendar.getTime();
    }

    public static Date parse(String date) throws ParseException {
        return parse(date, DEFAULT_DATE_PATTERN);
    }

    public static Date parse(String date, String pattern) throws ParseException {
        if (date != null) {
            return new SimpleDateFormat(pattern).parse(date);
        }
        return null;
    }

    public static String format(Date date) {
        return format(date, DEFAULT_DATE_PATTERN);
    }

    public static String format(Date date, String pattern) {
        if (date != null) {
            return new SimpleDateFormat(pattern).format(date);
        }
        return null;
    }

    public static String computeDateRangeString(Date begin, Date end, String patten, String split) {
        if (begin == null || end == null) {
            return null;
        }
        if (StringUtils.isBlank(patten)) {
            patten = DEFAULT_DATE_PATTERN;
        }
        if (StringUtils.isBlank(split)) {
            split = CharacterConstants.SEPARATOR_HYPHEN;
        }
        return DateHelper.format(begin, patten)
                + split
                + DateHelper.format(end, patten);
    }

    public static Date set(Date date, int value, int... timeUnits) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        for (int timeUnit : timeUnits) {
            calendar.set(timeUnit, value);
        }
        return calendar.getTime();
    }

    public static Date addValue(Date date, TimeUnit timeUnit, int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date == null ? new Date() : date);
        switch (timeUnit) {
            case SECONDS:
                calendar.add(Calendar.SECOND, value);
                break;
            case MINUTES:
                calendar.add(Calendar.MINUTE, value);
                break;
            case HOURS:
                calendar.add(Calendar.HOUR_OF_DAY, value);
                break;
            case DAYS:
                calendar.add(Calendar.DAY_OF_YEAR, value);
                break;
            default:
                throw new IllegalArgumentException("Invalid time unit. value = " + timeUnit);
        }
        return calendar.getTime();
    }
}
