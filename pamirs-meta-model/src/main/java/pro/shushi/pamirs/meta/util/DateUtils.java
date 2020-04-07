package pro.shushi.pamirs.meta.util;


import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.DateUnitEnum;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class DateUtils {

    public static String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyyMMdd = "yyyy-MM-dd";

    public static String OTHER = "EEE MMM dd HH:mm:ss zzz yyyy";


    /**
     * 时间调整
     *
     * @param date    时间
     * @param express 表达式 "YEAR:1,MONTH:1,DAY:1,WEEK:1,HOUR:1,MINUTE:1,SECOND:1"
     * @return
     */
    public static Date express(Date date, String express) {
        List<String> expList = Arrays.asList(express.split(","));
        for (String s : expList) {
            String[] exp = s.split(":");
            if (exp.length > 1) {
                date = add(date, DateUnitEnum.valueOf(exp[0]).value(), Integer.valueOf(exp[1]));
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
     * @return
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
     * @param date
     * @return yyyy-MM-dd HH:mm:ss 格式的时间字符串
     */
    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(yyyyMMddHHmmss);
        String dateStr = format.format(date);
        return dateStr;
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
        String dateStr = format.format(date);
        return dateStr;
    }

    /**
     * 格式化时间
     *
     * @param timeMillis 时间毫秒数
     * @param pattern    时间格式字符串 如：yyyy-MM-dd HH:mm:ss
     * @return 指定格式的时间字符串
     */
    public static String formatDate(Long timeMillis, String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(NumberUtils.valueOf(timeMillis));
        return formatDate(calendar.getTime(), pattern);
    }

    /**
     * 格式化时间
     * 根据字符串，模板转换时间
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date formatDate(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化时间
     * 根据字符串，模板转换时间
     *
     * @param dateStr
     * @return
     */
    public static Date unsafeFormatDate(String dateStr) {
        try {
            return new SimpleDateFormat(yyyyMMddHHmmss).parse(dateStr);
        } catch (Exception e) {
            SimpleDateFormat sdf = new SimpleDateFormat(OTHER, Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            try {
                return sdf.parse(dateStr);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
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
            return formatDate((String) dateObj, DateUtils.yyyyMMddHHmmss);
        }
        return null;
    }

    public static String convertDate(Object param){
        if(null != param){
            Date d;
            if(param instanceof Timestamp){
                d = new Date(((Timestamp) param).getTime());
            }else if(param instanceof Date){
                d = (Date)param;
            }else if(param instanceof Long){
                d = new Date((Long)param);
            }else{
                return (String)param;
            }
            return DateUtils.formatDate(d);
        }
        return null;
    }

    public static Date toDate(Object param){
        if(null != param){
            if(param instanceof Timestamp){
                return new Date(((Timestamp) param).getTime());
            }else if(param instanceof Date){
                return (Date)param;
            }else if(param instanceof Long){
                return new Date((Long)param);
            }else if(param instanceof String) {
                return unsafeFormatDate((String) param);
            }
            return null;
        }
        return null;
    }

    /**
     * Parse date by 'yyyy-MM-dd' pattern
     *
     * @param str
     * @return
     */
    public static Date parseByDayPattern(String str) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(str, yyyyMMdd);
        } catch (ParseException e) {
            return new Date();
        }
}

    /**
     * 比较与当前时间相差分钟数
     * @param timeMillis
     * @return
     */
    public static long compareWithNowMin(Long timeMillis) {
        return (System.currentTimeMillis() - timeMillis) / (1000 * 60);
    }

}
