package pro.shushi.pamirs.meta.util;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.DateUnitEnum;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_DATE_FORMAT_ERROR;

@Slf4j
public class DateUtils {

    public final static String TIME_SUFFIX = " 00:00:00"; //时间后缀
    public final static String[] REPLACE_STRING = new String[]{"GMT+0800", "GMT+08:00"};
    public final static String SPLIT_STRING = "(中国标准时间)";

    public static final DateTimeFormatter yyyyMMddHHmmss = DateTimeFormatter.ofPattern(DateFormatEnum.DATETIME.value());
    public static final DateTimeFormatter eeeMMddzzzyyyy = DateTimeFormatter.ofPattern(DateFormatEnum.DATETIME_EEE_ZZ.value());

    //时区常量
    public final static String TIME_ZONE_CST = "CST";
    public final static String TIME_ZONE_GMT = "GMT";
    public final static String TIME_ZONE_UTC = "UTC";

    // 创建Time的Pattern对象(基于正则表达式)，匹配：23:07:07 格式的时间
    public final static Pattern timePattern = Pattern.compile("^([01]?[0-9]|2[0-3]):([0-5]?[0-9]):([0-5]?[0-9])$");
    public final static Pattern yyMMddpattern = Pattern.compile("(\\d{1,2})-(\\d{1,2})-(\\d{2})");

    /**
     * 时间调整
     *
     * @param date    时间
     * @param express 表达式 "YEAR:1,MONTH:1,DAY:1,WEEK:1,HOUR:1,MINUTE:1,SECOND:1"
     * @return 时间
     */
    public static Date express(Date date, String express) {
        String[] expList = express.split(",");
        for (String s : expList) {
            String[] exp = s.split(":");
            if (exp.length > 1) {
                date = add(date, DateUnitEnum.valueOf(exp[0]).value(), Integer.parseInt(exp[1]));
            }
        }
        return date;
    }

    /**
     * 时间调整
     *
     * @param date   时间
     * @param unit   单位
     * @param number 调整值
     * @return 时间
     * @see DateFormatEnum
     */
    public static Date add(Date date, Integer unit, int number) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(unit, number);
        date = calendar.getTime();
        return date;
    }

    /**
     * 格式化时间
     *
     * @param date 时间
     * @return yyyy-MM-dd HH:mm:ss 格式的时间字符串
     */
    public static String formatDate(Date date) {
        return yyyyMMddHHmmss.format(
                LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
        );
    }

    /**
     * 格式化时间
     *
     * @param date      时间
     * @param formatter 格式转换
     * @return 指定格式的时间字符串
     */
    public static String formatDate(Date date, DateTimeFormatter formatter) {
        return formatter.format(
                LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
        );
    }

    /**
     * 格式化时间
     *
     * @param date    时间
     * @param pattern 时间格式字符串 如：yyyy-MM-dd HH:mm:ss
     * @return 指定格式的时间字符串
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 格式化时间
     *
     * @param dateStr   时间字符串
     * @param formatter 格式
     * @return 时间
     */
    public static Date formatDate(String dateStr, DateTimeFormatter formatter) {
        return Date.from(LocalDateTime.from(formatter.parse(dateStr)).atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 格式化时间
     *
     * @param timeMillis 时间毫秒数
     * @param pattern    时间格式字符串 如：yyyy-MM-dd HH:mm:ss
     * @return 指定格式的时间字符串
     */
    public static String formatDate(Long timeMillis, String pattern) {
        if (DateFormatEnum.TIMESTAMP.value().equals(pattern)) {
            return timeMillis.toString();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(NumberUtils.valueOf(timeMillis));
        return formatDate(calendar.getTime(), pattern);
    }

    /**
     * 格式化时间
     * 根据字符串，模板转换时间
     *
     * @param dateStr 时间字符串
     * @param pattern 格式
     * @return 时间
     */
    public static Date formatDate(String dateStr, String pattern) {
        return formatDate0(dateStr, new SimpleDateFormat(pattern));
    }

    /**
     * 格式化时间
     * 根据字符串，模板转换时间
     *
     * @param dateStr 时间字符串
     * @return 时间
     */
    public static Date unsafeFormatDate(String dateStr) {
        try {
            return formatDate(dateStr, yyyyMMddHHmmss);
        } catch (Exception e) {
            return formatDate(dateStr, eeeMMddzzzyyyy);
        }
    }

    private static Date formatDate0(String dateStr, SimpleDateFormat sdf) {
        try {
            return sdf.parse(dateStr);
        } catch (Exception ex) {
            throw PamirsException.construct(BASE_DATE_FORMAT_ERROR, ex).errThrow();
        }
    }

    /**
     * 格式化时间
     *
     * @param timeMillis 时间毫秒数
     * @return 指定时间戳的时间
     */
    public static Date formatDate(Long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(NumberUtils.valueOf(timeMillis));
        return calendar.getTime();
    }

    public static Date createDate(Object dateObj, String pattern) {
        if (dateObj == null)
            return null;
        if (dateObj instanceof Date) {
            return (Date) dateObj;
        } else if (dateObj instanceof String) {
            if (null == pattern) {
                pattern = DateFormatEnum.DATETIME.value();
            }
            return formatDate((String) dateObj, pattern);
        }
        return null;
    }

    public static String convertDate(Object param, String pattern) {
        if (null != param) {
            long d;
            if (param instanceof java.sql.Time) {
                // 前端定义为Time的字段类型的序列化(如: 23:07:07)
                return param.toString();
            } else if (param instanceof Date) {
                d = ((Date) param).getTime();
            } else if (param instanceof Long) {
                d = (Long) param;
            } else if(param instanceof Integer){
                d = (Integer) param;
            } else if (param instanceof String) {
                String paramString = (String) param;
                if (org.apache.commons.lang3.math.NumberUtils.isDigits(paramString)) {
                    d = Long.parseLong(paramString);
                } else {
                    return DateUtils.formatDate(DateUtils.convertToDate(paramString), pattern);
                }
            } else {
                throw new UnsupportedOperationException("Invalid param type.");
            }
            if (StringUtils.isBlank(pattern)) {
                pattern = DateFormatEnum.DATETIME.value();
            }
            return DateUtils.formatDate(d, pattern);
        }
        return null;
    }

    public static Date toDate(Object param) {
        if (null != param) {
            if (param instanceof Timestamp) {
                return new Date(((Timestamp) param).getTime());
            } else if (param instanceof java.sql.Date) {
                return new Date(((java.sql.Date) param).getTime());
            } else if (param instanceof java.sql.Time) {
                return new Date(((java.sql.Time) param).getTime());
            } else if (param instanceof Date) {
                return (Date) param;
            } else if (param instanceof Long) {
                return new Date((Long) param);
            } else if (param instanceof Integer) {
                return new Date((Integer) param);
            } else if (param instanceof String) {
                return unsafeFormatDate((String) param);
            }
            return null;
        }
        return null;
    }

    public static Object castDate(Object param, String ltype, String pattern) {
        if (null == param) {
            return null;
        }
        long dateLong;
        if (param instanceof Timestamp && !Timestamp.class.getName().equals(ltype)) {
            dateLong = ((Timestamp) param).getTime();
        } else if (param instanceof java.sql.Date && !java.sql.Date.class.getName().equals(ltype)) {
            dateLong = ((java.sql.Date) param).getTime();
        } else if (param instanceof java.sql.Time && !java.sql.Time.class.getName().equals(ltype)) {
            dateLong = ((java.sql.Time) param).getTime();
        } else if (param instanceof Date && !Date.class.getName().equals(ltype)) {
            dateLong = ((Date) param).getTime();
        } else if (param instanceof Long && !Long.class.getName().equals(ltype)) {
            dateLong = (Long) param;
        } else if (param instanceof Integer && !Integer.class.getName().equals(ltype)) {
            dateLong = (Integer) param;
        } else if (param instanceof String && !String.class.getName().equals(ltype)) {
            dateLong = Objects.requireNonNull(formatDate((String) param, pattern)).getTime();
        } else {
            return param;
        }
        if (Timestamp.class.getName().equals(ltype)) {
            return new Timestamp(dateLong);
        } else if (java.sql.Date.class.getName().equals(ltype)) {
            return new java.sql.Date(dateLong);
        } else if (java.sql.Time.class.getName().equals(ltype)) {
            return new java.sql.Time(dateLong);
        } else if (Date.class.getName().equals(ltype)) {
            return new Date(dateLong);
        } else if (Long.class.getName().equals(ltype)) {
            return dateLong;
        } else if (String.class.getName().equals(ltype)) {
            return formatDate(dateLong, pattern);
        } else {
            return param;
        }
    }

    public static long getTime(Object param) {
        long dateLong;
        if (param instanceof Timestamp) {
            dateLong = ((Timestamp) param).getTime();
        } else if (param instanceof java.sql.Date) {
            dateLong = ((java.sql.Date) param).getTime();
        } else if (param instanceof java.sql.Time) {
            dateLong = ((java.sql.Time) param).getTime();
        } else if (param instanceof Date) {
            dateLong = ((Date) param).getTime();
        } else if (param instanceof Long) {
            dateLong = (Long) param;
        } else if (param instanceof String) {
            dateLong = Long.parseLong((String) param);
        } else {
            return (long) param;
        }
        return dateLong;
    }

    /**
     * Parse date by 'yyyy-MM-dd' pattern
     *
     * @param str 时间字符串
     * @return 时间
     */
    public static Date parseByDayPattern(String str) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(str, DateFormatEnum.DATE.value());
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 比较与当前时间相差分钟数
     *
     * @param timeMillis timeMillis
     * @return 相差分钟数
     */
    public static long compareWithNowMin(Long timeMillis) {
        return (System.currentTimeMillis() - timeMillis) / (1000 * 60);
    }

    // 日期加（排除掉周末）
    public static Date addWorkDay(Date date, int days) {
        if (days > 0) {
            return addDaysSkipWeekend(date, days, 1);
        } else {
            return addDaysSkipWeekend(date,  -days, -1);
        }
    }

    private static Date addDaysSkipWeekend(Date date, int days, int perPlus) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        // 增加天数
        for (int i = 0; i < days; i++) {
            // 增加天数
            calendar.add(Calendar.DATE, perPlus);
            // 判断日期类型，是否周末
            boolean isWeekend = isWeekend(calendar);
            if (isWeekend) {
                i--;
            }
        }
        return calendar.getTime();
    }

    /**
     * 判断是否是周末
     * @return
     */
    private static boolean isWeekend(Calendar cal) {
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week == 6 || week == 0) {//0代表周日，6代表周六
            return true;
        }
        return false;
    }

    /**
     * 指定日期当天最小时间(即当天的启始时间)
     * @param date
     * @return
     */
    public static Date getMinTimeOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     *  dateStr在表达式中存在这样的格式：
     *      Sat Dec 12 00:00:00 CST 2022
     *      Wed Jul 20 14:41:05 GMT+08:00 2022
     *      Tue Aug 21 2018 00:00:00 GMT+0800 (中国标准时间) 00:00:00
     * @param dateStr
     * @return
     */
    public static Date convertToDate(String dateStr) {
        if (StringUtils.isBlank(dateStr) || "null".equalsIgnoreCase(dateStr)) {
            return null;
        }

        int length = dateStr.length();
        try {
            if (length <= 8) {
                // 传入是时间(如：23:07:07)的序列化
                Boolean timeMatcher = timePattern.matcher(dateStr).matches();
                if (timeMatcher) {
                    return parseTimeStr(dateStr);
                } else {
                    // 传入是日期(如：22-07-18或者2022-07-18)的序列化
                    Boolean yyMMddMather = yyMMddpattern.matcher(dateStr).matches();
                    if (yyMMddMather) {
                        return parseYyMMdd(dateStr);
                    }
                }
            } else {
                //如果传入的是日期，就统一转换为日期-时间来比较(2024-03-03)
                if (dateStr.trim().length() < 11) {
                    dateStr = dateStr + TIME_SUFFIX;
                }
            }

            if (dateStr.contains(SPLIT_STRING)) {
                dateStr = dateStr.split(Pattern.quote(SPLIT_STRING))[0].replace(REPLACE_STRING[0], REPLACE_STRING[1]);
                SimpleDateFormat sf1 = new SimpleDateFormat(DateFormatEnum.DATETIME_E_MM_Z.value(), Locale.US);
                return sf1.parse(dateStr);
            } else if (dateStr.contains(TIME_ZONE_CST)
                    || dateStr.contains(TIME_ZONE_GMT)
                    || dateStr.contains(TIME_ZONE_UTC)) {
                //格式化CST时间
                SimpleDateFormat sdf2 = new SimpleDateFormat(DateFormatEnum.DATETIME_EEE_ZZ.value(), Locale.US);
                return sdf2.parse(dateStr);
            } else {
                return DateUtils.formatDate(dateStr, DateUtils.yyyyMMddHHmmss);
            }
        } catch (Exception e) {
            throw PamirsException.construct(BASE_DATE_FORMAT_ERROR, e).errThrow();
        }
    }

    public static Date convertFormatDate(String dateStr, String pattern) {
        if (StringUtils.isBlank(dateStr) || "null".equalsIgnoreCase(dateStr)) {
            return null;
        }

        if (dateStr.contains(SPLIT_STRING)) {
            try {
                dateStr = dateStr.split(Pattern.quote(SPLIT_STRING))[0].replace(REPLACE_STRING[0], REPLACE_STRING[1]);
                SimpleDateFormat sf1 = new SimpleDateFormat(DateFormatEnum.DATETIME_E_MM_Z.value(), Locale.US);
                Date gmtDate =  sf1.parse(dateStr);
                String gmtDataStr = new SimpleDateFormat(DateFormatEnum.DATETIME.value()).format(gmtDate);
                return DateUtils.formatDate(gmtDataStr, pattern);
            } catch (Exception e) {
                throw PamirsException.construct(BASE_DATE_FORMAT_ERROR, e).errThrow();
            }
        } else if (dateStr.contains(TIME_ZONE_CST)
                || dateStr.contains(TIME_ZONE_GMT)
                || dateStr.contains(TIME_ZONE_UTC)) {
            try {
                SimpleDateFormat sdf2 = new SimpleDateFormat(DateFormatEnum.DATETIME_EEE_ZZ.value(), Locale.US);
                Date cstDate = sdf2.parse(dateStr);
                String cstDataStr = new SimpleDateFormat(DateFormatEnum.DATETIME.value()).format(cstDate);
                return DateUtils.formatDate(cstDataStr, pattern);
            } catch (Exception e) {
                throw PamirsException.construct(BASE_DATE_FORMAT_ERROR, e).errThrow();
            }
        } else {
            return DateUtils.formatDate(dateStr, pattern);
        }
    }

    public static Date convertToDateBegin(String dateStr) {
        Date date = DateUtils.convertToDate(dateStr);
        if (date==null) {
            return null;
        }
        return DateUtils.getMinTimeOfDay(date);
    }

    /**
     * 两个时间差得到秒
     *
     * @param endDate
     * @param startDate
     * @return
     */
    public static Long subDateTimeToSeconds(Date endDate, Date startDate) {
        long eTime = endDate.getTime();
        long sTime = startDate.getTime();
        long diff = (eTime - sTime) / 1000;
        return diff;
    }

    /**
     * 毫秒 转时分秒(HH:mm:ss)
     *
     * @param second 秒(不是毫秒数)
     * @return HH:mm:ss
     */
    public static String secondsToDHMS(Long second) {
        long days = second /86400;//转换天数
        second = second % 86400;//剩余秒数
        long hours = second /3600;//转换小时数
        second = second % 3600;//剩余秒数
        long minutes = second /60;//转换分钟
        second = second % 60;//剩余秒数
        if (0 < days){
            return days + "天" + hours + "小时" + minutes + "分" + second + "秒";
        } else {
            return hours + "小时" + minutes + "分" + second + "秒";
        }
    }

    public static Date parseTimeStr(String timeStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return new Time(dateFormat.parse(timeStr).getTime());
    }

    public static Date parseYyMMdd(String dataStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        return dateFormat.parse(dataStr);
    }

    public static void main(String[] args) {
        Date date1 = DateUtils.getMinTimeOfDay(DateUtils.formatDate("2022-07-18 23:00:10", DateUtils.yyyyMMddHHmmss));
        Date date2 = DateUtils.getMinTimeOfDay(DateUtils.formatDate("2022-07-19 01:00:50", DateUtils.yyyyMMddHHmmss));
        Long diffDay = (date2.getTime() - date1.getTime())/(24*60*60*1000);
        System.out.println(diffDay);
        System.out.println("=================");
        Date date31 = convertToDate("Tue Aug 21 2018 00:00:00 GMT+0800 (中国标准时间) 00:00:00");
        Date date32= convertFormatDate("Tue Aug 21 2018 00:00:00 GMT+0800 (中国标准时间) 00:00:00", DateFormatEnum.DATETIME.value());
        System.out.println(date31);
        System.out.println(date32);
        System.out.println("=================");
        Date date41 = convertToDate("Wed Jul 20 14:41:05 GMT+08:00 2022");
        Date date42 = convertFormatDate("Wed Jul 20 14:41:05 GMT+08:00 2022", DateFormatEnum.DATETIME.value());
        System.out.println(date41);
        System.out.println(date42);
        System.out.println("=================");
        Date date51 = convertToDate("Sat Dec 12 00:00:00 CST 2022");
        Date date52 = convertFormatDate("Sat Dec 12 00:00:00 CST 2022", DateFormatEnum.DATETIME.value());
        System.out.println(date51);
        System.out.println(date52);
        System.out.println("=================");
        Date date61 = convertToDate("2022-07-18 23:00:10");
        Date date62 = convertToDate("2022-07-18");
        System.out.println(date61);
        System.out.println(date62);
        System.out.println("=================");

        System.out.printf(DateUtils.convertToDate("13:18:18") + "");
        System.out.printf(DateUtils.convertToDate("2022-07-18") + "");
        System.out.printf(DateUtils.convertToDate("22-07-18") + "");
    }

}
