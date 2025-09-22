package pro.shushi.pamirs.framework.faas.fun.builtin;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.util.DateUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import static pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum.TIME;
import static pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum.JAVA;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;
import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.EXPRESSION;

/**
 * 时间函数
 *
 * @version 1.0.0
 */
@Fun(NamespaceConstants.expression)
public class DateFunctions {

    @Function.Advanced(
            displayName = "返回当前时间", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("NOW")
    @Function(name = "NOW", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: NOW()\n函数说明: 返回当前时间"
    )
    public static Date now() {
        return new Date();
    }

    @Function.Advanced(
            displayName = "返回当前时间字符串", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("NOW_STR")
    @Function(name = "NOW_STR", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: NOW_STR()\n函数说明: 返回当前时间字符串，精确到时分秒，格式为yyyy-MM-dd hh:mm:ss"
    )
    public static String nowStr() {
        return DateUtils.formatDate(new Date());
    }

    @Function.Advanced(
            displayName = "返回今天的日期字符串", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("TODAY_STR")
    @Function(name = "TODAY_STR", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: TODAY_STR()\n函数说明: 返回今天的日期字符串，精确到天，格式为yyyy-MM-dd"
    )
    public static String todayStr() {
        return DateUtils.formatDate(new Date(), "yyyy-MM-dd");
    }

    @Function.Advanced(
            displayName = "加减指定天数", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("ADD_DAY")
    @Function(name = "ADD_DAY", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: ADD_DAY(date,days)\n" +
                    "函数说明: 将指定日期加/减指定天数，date为指定日期，days为指定天数，当为负数时在date上减去此天数"
    )
    public static Date addDay(Date date, Long days) {
        if (null == date) {
            return null;
        }
        if (null == days) {
            return date;
        }
        return DateUtils.add(date, Calendar.DATE, days.intValue());
    }

    public static Date addDay(Date date, Integer days) {
        if (null == date) {
            return null;
        }
        if (null == days) {
            return date;
        }
        return DateUtils.add(date, Calendar.DATE, days);
    }

    public static Date addDay(java.sql.Date date, Long days) {
        if (null == date) {
            return null;
        }
        if (null == days) {
            return date;
        }
        return DateUtils.add(date, Calendar.DATE, days.intValue());
    }

    public static Date addDay(java.sql.Date date, Integer days) {
        if (null == date) {
            return null;
        }
        if (null == days) {
            return date;
        }
        return DateUtils.add(date, Calendar.DATE, days);
    }

    public static Date addDay(String date, Long days) {
        if (null == date) {
            return null;
        }
        Date dataObj = DateUtils.convertFormatDate(date, DateFormatEnum.DATETIME.value());
        return addDay(dataObj, days);
    }

    public static Date addDay(String date, Integer days) {
        if (null == date) {
            return null;
        }
        Date dataObj = DateUtils.convertFormatDate(date, DateFormatEnum.DATETIME.value());
        return addDay(dataObj, days);
    }

    @Function.Advanced(
            displayName = "加减指定月数", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("ADD_MONTH")
    @Function(name = "ADD_MONTH", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: ADD_MONTH(date,months)\n" +
                    "函数说明: 将指定日期加/减指定月数，date为指定日期，months为指定月数，当为负数时在此date上减去此月数"
    )
    public static Date addMonth(Date date, Long months) {
        if (null == date) {
            return null;
        }
        if (null == months) {
            return date;
        }
        return DateUtils.add(date, Calendar.MONTH, months.intValue());
    }

    public static Date addMonth(Date date, Integer months) {
        if (null == date) {
            return null;
        }
        if (null == months) {
            return date;
        }
        return DateUtils.add(date, Calendar.MONTH, months);
    }

    public static Date addMonth(java.sql.Date date, Long months) {
        if (null == date) {
            return null;
        }
        if (null == months) {
            return date;
        }
        return DateUtils.add(date, Calendar.MONTH, months.intValue());
    }

    public static Date addMonth(java.sql.Date date, Integer months) {
        if (null == date) {
            return null;
        }
        if (null == months) {
            return date;
        }
        return DateUtils.add(date, Calendar.MONTH, months);
    }

    public static Date addMonth(String date, Long months) {
        if (null == date) {
            return null;
        }
        Date dataObj = DateUtils.convertFormatDate(date, DateFormatEnum.DATETIME.value());
        return addMonth(dataObj, months);
    }

    public static Date addMonth(String date, Integer months) {
        if (null == date) {
            return null;
        }
        Date dataObj = DateUtils.convertFormatDate(date, DateFormatEnum.DATETIME.value());
        return addMonth(dataObj, months);
    }

    @Function.Advanced(
            displayName = "加减指定年数", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("ADD_YEAR")
    @Function(name = "ADD_YEAR", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: ADD_YEAR(date,years)\n" +
                    "函数说明: 将指定日期加/减指定年数，date为指定日期，years为指定年数，当为负数时在此date上减去此年数"
    )
    public static Date addYear(Date date, Long years) {
        if (null == date) {
            return null;
        }
        if (null == years) {
            return date;
        }
        return DateUtils.add(date, Calendar.YEAR, years.intValue());
    }

    public static Date addYear(Date date, Integer years) {
        if (null == date) {
            return null;
        }
        if (null == years) {
            return date;
        }
        return DateUtils.add(date, Calendar.YEAR, years);
    }

    public static Date addYear(java.sql.Date date, Long years) {
        if (null == date) {
            return null;
        }
        if (null == years) {
            return date;
        }
        return DateUtils.add(date, Calendar.YEAR, years.intValue());
    }

    public static Date addYear(java.sql.Date date, Integer years) {
        if (null == date) {
            return null;
        }
        if (null == years) {
            return date;
        }
        return DateUtils.add(date, Calendar.YEAR, years);
    }

    public static Date addYear(String date, Long years) {
        if (null == date) {
            return null;
        }
        Date dataObj = DateUtils.convertFormatDate(date, DateFormatEnum.DATETIME.value());
        return addYear(dataObj, years);
    }

    public static Date addYear(String date, Integer years) {
        if (null == date) {
            return null;
        }
        Date dataObj = DateUtils.convertFormatDate(date, DateFormatEnum.DATETIME.value());
        return addYear(dataObj, years);
    }

    @Function.Advanced(
            displayName = "大于", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("GREATER_THAN")
    @Function(name = "GREATER_THAN", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: GREATER_THAN(date1,date2)\n函数说明: 比较两个日期的大小，date1 > date2时返回true"
    )
    public static Boolean greaterThan(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new Date(0);
        }
        if (date2 == null) {
            date2 = new Date(0);
        }
        return date1.after(date2);
    }

    public static Boolean greaterThan(java.sql.Date date1, java.sql.Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new java.sql.Date(0);
        }
        if (date2 == null) {
            date2 = new java.sql.Date(0);
        }
        return date1.after(date2);
    }

    public static Boolean greaterThan(java.sql.Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new java.sql.Date(0);
        }

        java.sql.Date _date2 = null;
        if (date2 == null) {
            _date2 = new java.sql.Date(0);
        } else {
            _date2 = new java.sql.Date(date2.getTime());
        }
        return date1.after(_date2);
    }

    public static Boolean greaterThan(String dateStr1, String dateStr2) {
        return compareDateStr(dateStr1, dateStr2, "gt");
    }

    @Function.Advanced(
            displayName = "大于等于", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("GREATER_EQUAL")
    @Function(name = "GREATER_EQUAL", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: GREATER_EQUAL(date1,date2)\n函数说明: 比较两个日期的大小，date1 >= date2时返回true"
    )
    public static Boolean greaterEquals(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new Date(0);
        }
        if (date2 == null) {
            date2 = new Date(0);
        }
        return date1.after(date2) || date1.equals(date2);
    }

    public static Boolean greaterEquals(java.sql.Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new java.sql.Date(0);
        }

        java.sql.Date _date2 = null;
        if (date2 == null) {
            _date2 = new java.sql.Date(0);
        } else {
            _date2 = new java.sql.Date(date2.getTime());
        }
        return date1.after(_date2) || date1.equals(_date2);
    }

    public static Boolean greaterEquals(java.sql.Date date1, java.sql.Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new java.sql.Date(0);
        }
        if (date2 == null) {
            date2 = new java.sql.Date(0);
        }
        return date1.after(date2) || date1.equals(date2);
    }

    public static Boolean greaterEquals(String dateStr1, String dateStr2) {
        return compareDateStr(dateStr1, dateStr2, "ge");
    }

    @Function.Advanced(
            displayName = "小于", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("LESS_THAN")
    @Function(name = "LESS_THAN", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LESS_THAN(date1,date2)\n函数说明: 比较两个日期的大小，date1 < date2时返回true"
    )
    public static Boolean lessThan(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new Date(0);
        }
        if (date2 == null) {
            date2 = new Date(0);
        }
        return date1.before(date2);
    }

    public static Boolean lessThan(java.sql.Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new java.sql.Date(0);
        }

        java.sql.Date _date2 = null;
        if (date2 == null) {
            _date2 = new java.sql.Date(0);
        } else {
            _date2 = new java.sql.Date(date2.getTime());
        }
        return date1.before(_date2);
    }

    public static Boolean lessThan(java.sql.Date date1, java.sql.Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new java.sql.Date(0);
        }
        if (date2 == null) {
            date2 = new java.sql.Date(0);
        }
        return date1.before(date2);
    }

    public static Boolean lessThan(String dateStr1, String dateStr2) {
        return compareDateStr(dateStr1, dateStr2, "lt");
    }

    @Function.Advanced(
            displayName = "小于等于", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("LESS_EQUAL")
    @Function(name = "LESS_EQUAL", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LESS_EQUAL(date1,date2)\n函数说明: 比较两个日期的大小，date1 <= date2时返回true"
    )
    public static Boolean lessEquals(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new Date(0);
        }
        if (date2 == null) {
            date2 = new Date(0);
        }
        return date1.before(date2) || date1.equals(date2);
    }

    public static Boolean lessEquals(java.sql.Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new java.sql.Date(0);
        }

        java.sql.Date _date2 = null;
        if (date2 == null) {
            _date2 = new java.sql.Date(0);
        } else {
            _date2 = new java.sql.Date(date2.getTime());
        }
        return date1.before(_date2) || date1.equals(_date2);
    }

    public static Boolean lessEquals(java.sql.Date date1, java.sql.Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new java.sql.Date(0);
        }
        if (date2 == null) {
            date2 = new java.sql.Date(0);
        }
        return date1.before(date2) || date1.equals(date2);
    }

    public static Boolean lessEquals(String dateStr1, String dateStr2) {
        return compareDateStr(dateStr1, dateStr2, "le");
    }

    @Function.Advanced(
            displayName = "等于", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("DATE_EQUALS")
    @Function(name = "DATE_EQUALS", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: DATE_EQUALS(date1,date2)\n函数说明: 比较两个日期的大小，date1 == date2时返回true"
    )
    public static Boolean dateEquals(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new Date(0);
        }
        if (date2 == null) {
            date2 = new Date(0);
        }
        return date1.equals(date2);
    }

    public static Boolean dateEquals(java.sql.Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new java.sql.Date(0);
        }

        java.sql.Date _date2 = null;
        if (date2 == null) {
            _date2 = new java.sql.Date(0);
        } else {
            _date2 = new java.sql.Date(date2.getTime());
        }
        return date1.equals(_date2);
    }

    public static Boolean dateEquals(java.sql.Date date1, java.sql.Date date2) {
        if (date1 == null && date2 == null) {
            return Boolean.FALSE;
        }
        if (date1 == null) {
            date1 = new java.sql.Date(0);
        }
        if (date2 == null) {
            date2 = new java.sql.Date(0);
        }
        return date1.equals(date2);
    }

    public static Boolean dateEquals(String dateStr1, String dateStr2) {
        return compareDateStr(dateStr1, dateStr2, "equal");
    }

    public static Boolean dateEquals(java.sql.Date date1, String date2) {
        Date convertedDate1 = convertToStandardDate(date1);
        Date convertedDate2 = convertStringToDate(date2);
        return dateEquals(convertedDate1, convertedDate2);
    }

    public static Boolean dateEquals(Date date1, String date2) {
        Date convertedDate2 = convertStringToDate(date2);
        return dateEquals(date1, convertedDate2);
    }

    public static Boolean dateEquals(String date1, java.sql.Date date2) {
        Date convertedDate1 = convertStringToDate(date1);
        Date convertedDate2 = convertToStandardDate(date2);
        return dateEquals(convertedDate1, convertedDate2);
    }

    public static Boolean dateEquals(String date1, Date date2) {
        Date convertedDate1 = convertStringToDate(date1);
        return dateEquals(convertedDate1, date2);
    }

    private static Date convertStringToDate(String dateStr) {
        if (StringUtils.isBlank(dateStr) || "null".equalsIgnoreCase(dateStr)) {
            return null;
        }
        return DateUtils.convertToDate(dateStr);
    }

    private static Date convertToStandardDate(java.sql.Date sqlDate) {
        return sqlDate != null ? new Date(sqlDate.getTime()) : new Date(0);
    }

    @Function.Advanced(
            displayName = "转换为时间", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("TO_DATE")
    @Function(name = "TO_DATE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: TO_DATE(date,pattern)\n函数说明: 将date字符串按格式转换为时间"
    )
    public static Date formatDate(String date, String pattern) {
        return DateUtils.convertFormatDate(date, pattern);
    }

    @Function.Advanced(
            displayName = "工作日加减天数(跳过周末)", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("ADD_WORK_DAY")
    @Function(name = "ADD_WORK_DAY", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: ADD_WORK_DAY(date,days)\n" +
                    "函数说明: 将指定工作日加/减指定天数(跳过周末)，date为指定日期，days为指定天数，当为负数时在date上减去此天数"
    )
    public static Date addWorkDay(Date date, Long days) {
        if (null == date) {
            return null;
        }
        if (null == days) {
            return date;
        }
        return DateUtils.addWorkDay(date, days.intValue());
    }

    public static Date addWorkDay(Date date, Integer days) {
        if (null == date) {
            return null;
        }
        if (null == days) {
            return date;
        }
        return DateUtils.addWorkDay(date, days);
    }

    public static Date addWorkDay(java.sql.Date date, Long days) {
        if (null == date) {
            return null;
        }
        if (null == days) {
            return date;
        }
        return DateUtils.addWorkDay(date, days.intValue());
    }

    public static Date addWorkDay(java.sql.Date date, Integer days) {
        if (null == date) {
            return null;
        }
        if (null == days) {
            return date;
        }
        return DateUtils.addWorkDay(date, days);
    }

    public static Date addWorkDay(String date, Long days) {
        if (StringUtils.isBlank(date) || "null".equalsIgnoreCase(date)) {
            return null;
        }
        Date dataObj = DateUtils.convertFormatDate(date, DateFormatEnum.DATETIME.value());
        return addWorkDay(dataObj, days);
    }

    public static Date addWorkDay(String date, Integer days) {
        if (StringUtils.isBlank(date) || "null".equalsIgnoreCase(date)) {
            return null;
        }
        Date dataObj = DateUtils.convertFormatDate(date, DateFormatEnum.DATETIME.value());
        return addWorkDay(dataObj, days);
    }

    @Function.Advanced(
            displayName = "日期相隔天数", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("COUNT_DAY")
    @Function(name = "COUNT_DAY", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: COUNT_DAY(date1,date2)\n函数说明: 计算两个时间的间隔天数，date1的日期-date2的日期"
    )
    public static Long countDay(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return 0L;
        }
        if (date1 == null) {
            date1 = new Date(0);
        }
        if (date2 == null) {
            date2 = new Date(0);
        }

        return (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000);
    }

    public static Long countDay(java.sql.Date date1, java.sql.Date date2) {
        if (date1 == null && date2 == null) {
            return 0L;
        }
        if (date1 == null) {
            date1 = new java.sql.Date(0);
        }
        if (date2 == null) {
            date2 = new java.sql.Date(0);
        }

        return (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000);
    }

    public static Long countDay(String dateStr1, String dateStr2) {
        Date date1 = DateUtils.convertToDateBegin(dateStr1);
        Date date2 = DateUtils.convertToDateBegin(dateStr2);

        return countDay(date1, date2);
    }

    @Function.Advanced(
            displayName = "时间相减(得到秒)", language = JAVA,
            builtin = true, category = TIME)
    @Function.fun("SUB_DATETIME_TO_SECOND")
    @Function(name = "SUB_DATETIME_TO_SECOND", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: SUB_DATE_TO_SECOND(endDateTime,startDateTime)\n" +
                    "函数说明: 将指定时间相减得到秒，endDateTime为结束时间,startDateTime为开始时间")
    public static Long subDateTimeToSecond(Date endDate, Date startDate) {
        if (null == endDate) {
            return null;
        }
        if (null == startDate) {
            return endDate.getTime() / 1000;
        }
        return DateUtils.subDateTimeToSeconds(endDate, startDate);
    }

    public static Long subDateTimeToSecond(java.sql.Date endDate, java.sql.Date startDate) {
        if (null == endDate) {
            return null;
        }
        if (null == startDate) {
            return endDate.getTime() / 1000;
        }
        return DateUtils.subDateTimeToSeconds(endDate, startDate);
    }

    public static Long subDateTimeToSecond(String endDate, String startDate) {
        if (StringUtils.isBlank(startDate) || "null".equalsIgnoreCase(startDate) ||
                StringUtils.isBlank(endDate) || "null".equalsIgnoreCase(endDate)) {
            return 0L;
        }
        Date endDateObj = DateUtils.convertFormatDate(endDate, DateFormatEnum.DATETIME.value());
        Date startDateObj = DateUtils.convertFormatDate(startDate, DateFormatEnum.DATETIME.value());
        return subDateTimeToSecond(endDateObj, startDateObj);
    }

    @Function.Advanced(
            displayName = "时间相减(得到：DD天HH小时MM分SS秒)", language = JAVA,
            builtin = true, category = TIME)
    @Function.fun("SUB_DATETIME_TO_DDHHMMSS")
    @Function(name = "SUB_DATETIME_TO_DDHHMMSS", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: SUB_DATETIME_TO_DDHHMMSS(endDateTime,startDateTime)\n" +
                    "函数说明: 将指定时间相减得到DD天HH小时MM分SS秒，endDateTime为结束时间,startDateTime为开始时间")
    public static String subDateTimeToDHMS(Date endDate, Date startDate) {
        if (null == endDate) {
            return null;
        }
        if (null == startDate) {
            return DateUtils.secondsToDHMS(endDate.getTime() / 1000);
        }
        return DateUtils.secondsToDHMS(DateUtils.subDateTimeToSeconds(endDate, startDate));
    }

    public static String subDateTimeToDHMS(java.sql.Date endDate, java.sql.Date startDate) {
        if (null == endDate) {
            return null;
        }
        if (null == startDate) {
            return DateUtils.secondsToDHMS(endDate.getTime() / 1000);
        }
        return DateUtils.secondsToDHMS(DateUtils.subDateTimeToSeconds(endDate, startDate));
    }

    public static String subDateTimeToDHMS(String endDate, String startDate) {
        if (StringUtils.isBlank(startDate) || "null".equalsIgnoreCase(startDate) ||
                StringUtils.isBlank(endDate) || "null".equalsIgnoreCase(endDate)) {
            return "";
        }
        Date endDateObj = DateUtils.convertFormatDate(endDate, DateFormatEnum.DATETIME.value());
        Date startDateObj = DateUtils.convertFormatDate(startDate, DateFormatEnum.DATETIME.value());
        return DateUtils.secondsToDHMS(DateUtils.subDateTimeToSeconds(endDateObj, startDateObj));
    }

    private static Boolean compareDateStr(String dateStr1, String dateStr2, String type) {
        Date date1 = DateUtils.convertToDate(dateStr1);
        Date date2 = DateUtils.convertToDate(dateStr2);
        switch (type) {
            case "gt":    //大于
                return greaterThan(date1, date2);
            case "ge":    //大于等于
                return greaterThan(date1, date2) || dateEquals(date1, date2);
            case "lt":    //小于
                return lessThan(date1, date2);
            case "le":    //小于等于
                return lessThan(date1, date2) || dateEquals(date1, date2);
            case "equal":   //等于
                return dateEquals(date1, date2);
            default:
                return null;
        }
    }

    @Function.Advanced(
            displayName = "提取年", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("YEAR")
    @Function(name = "YEAR", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: YEAR(date)\n函数说明: 提取年"
    )
    public static Integer year(Object date) {
        if (null == date) {
            return null;
        }

        Instant instant = null;

        if (date instanceof java.sql.Date) {
            instant = ((java.sql.Date) date).toInstant();
        } else if (date instanceof Timestamp) {
            instant = ((Timestamp) date).toInstant();
        } else if (date instanceof Date) {
            instant = ((Date) date).toInstant();
        } else if (date instanceof String) {
            Date dataObj = DateUtils.convertFormatDate((String) date, DateFormatEnum.DATETIME.value());
            if (null == dataObj) {
                return null;
            }
            instant = dataObj.toInstant();
        }

        if (null == instant) {
            return null;
        }

        return instant
                .atZone(ZoneId.systemDefault())
                .getYear();
    }

    @Function.Advanced(
            displayName = "提取月", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("MONTH")
    @Function(name = "MONTH", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: MONTH(date)\n函数说明: 提取月"
    )
    public static Integer month(Object date) {
        if (null == date) {
            return null;
        }

        Instant instant = null;

        if (date instanceof java.sql.Date) {
            instant = ((java.sql.Date) date).toInstant();
        } else if (date instanceof Timestamp) {
            instant = ((Timestamp) date).toInstant();
        } else if (date instanceof Date) {
            instant = ((Date) date).toInstant();
        } else if (date instanceof String) {
            Date dataObj = DateUtils.convertFormatDate((String) date, DateFormatEnum.DATETIME.value());
            if (null == dataObj) {
                return null;
            }
            instant = dataObj.toInstant();
        }

        if (null == instant) {
            return null;
        }

        return instant
                .atZone(ZoneId.systemDefault())
                .getMonthValue();
    }

    @Function.Advanced(
            displayName = "提取日", language = JAVA,
            builtin = true, category = TIME
    )
    @Function.fun("DAY")
    @Function(name = "DAY", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: DAY(date)\n函数说明: 提取日"
    )
    public static Integer day(Object date) {
        if (null == date) {
            return null;
        }

        Instant instant = null;

        if (date instanceof java.sql.Date) {
            instant = ((java.sql.Date) date).toInstant();
        } else if (date instanceof Timestamp) {
            instant = ((Timestamp) date).toInstant();
        } else if (date instanceof Date) {
            instant = ((Date) date).toInstant();
        } else if (date instanceof String) {
            Date dataObj = DateUtils.convertFormatDate((String) date, DateFormatEnum.DATETIME.value());
            if (null == dataObj) {
                return null;
            }
            instant = dataObj.toInstant();
        }

        if (null == instant) {
            return null;
        }

        return instant
                .atZone(ZoneId.systemDefault())
                .getDayOfMonth();
    }

}
