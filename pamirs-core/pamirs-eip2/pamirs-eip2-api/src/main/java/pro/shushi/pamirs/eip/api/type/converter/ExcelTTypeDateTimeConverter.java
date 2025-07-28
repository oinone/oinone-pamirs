package pro.shushi.pamirs.eip.api.type.converter;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.DateUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gesi at 16:00 on 2025/7/18
 */
@Component
public class ExcelTTypeDateTimeConverter implements ExcelTTypeConverter {

    private static final String[][] COMMON_DATE_FORMAT_PATTERNS = {
            // 日期 + 时间（到秒）
            {"^\\d{4}[-/.年]\\d{1,2}[-/.月]\\d{1,2}[日]?\\s*\\d{1,2}[:时]\\d{1,2}[:分]\\d{1,2}[秒]?$", "yyyy-M-d H:m:s"},
            // 日期 + 时间（到分）
            {"^\\d{4}[-/.年]\\d{1,2}[-/.月]\\d{1,2}[日]?\\s*\\d{1,2}[:时]\\d{1,2}([分])?$", "yyyy-M-d H:m"},
            // 仅日期
            {"^\\d{4}[-/.年]\\d{1,2}[-/.月]\\d{1,2}[日]?$", "yyyy-M-d"},
            {"^\\d{4}[-/.年]\\d{1,2}[-/.月]?$", "yyyy-M"}
    };

    private static final Pattern EXCEL_SERIAL_DATE_TIME = Pattern.compile("^(\\d*)\\.(\\d*)$");

    @Override
    public boolean canConvert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        return TtypeEnum.DATETIME.value().equals(excelTTypeDescriptor.getTargetType());
    }

    @Override
    public String convert(ExcelTTypeDescriptor excelTTypeDescriptor) {
        String value = excelTTypeDescriptor.getValue();
        String format = excelTTypeDescriptor.getFormat();
        try {
            Date date = getDateByString(value, format);
            return DateUtils.formatDate(date, DateFormatEnum.DATETIME.value());
        } catch (Exception e) {
            return defaultValue();
        }
    }

    @Override
    public String defaultValue() {
        return "2025-01-01 00:00:00";
    }

    public Date getDateByString(String value, String format) {
        Date date = null;
        value = value.trim();
        if (StringUtils.isNotBlank(format)) {
            format = format.replaceAll("\\[\\$-[^]]+]", "") // 去掉 [$-xxx]
                    .replaceAll(";@", "") // 去掉 ;@
                    .replaceAll("\\\\+", "") // 去掉 \
                    .trim();
        }
        if (ExcelDateFormatParser.EXCEL_DATA_MAPPING.containsKey(format) || ExcelDateFormatParser.EXCEL_DATA_MAPPING.containsKey(ExcelDateFormatParser.normalize(format))) {
            date = ExcelDateFormatParser.parse(value, format);
        }

        if (date == null) {
            if (value.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
                date = DateUtils.convertFormatDate(value, "yyyy-M-d H:m:s");
            } else if (value.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
                date = DateUtils.convertFormatDate(value, "yyyy-M-d H:m");
            } else if (value.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
                date = DateUtils.convertFormatDate(value, "yyyy-M-d");
            } else if (value.matches("^\\d{4}-\\d{1,2}$")) {
                date = DateUtils.convertFormatDate(value, "yyyy-M");
            } else if (value.matches("^\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
                date = DateUtils.convertFormatDate(value, "H:m:s");
            } else if (value.matches("^\\d{1,2}:\\d{1,2}$")) {
                date = DateUtils.convertFormatDate(value, "H:m");
            } else if (value.matches("^\\d{4}$")) {
                date = DateUtils.convertFormatDate(value, "yyyy");
            } else {
                // 非 yyyy-MM-dd HH:mm:ss 的常见时间格式
                for (String[] pattern : COMMON_DATE_FORMAT_PATTERNS) {
                    if (value.matches(pattern[0])) {
                        // 替换中文单位为英文分隔符以便解析
                        String normalized = value
                                .replaceAll("[年|月]", "-")
                                .replaceAll("日", "")
                                .replaceAll("时", ":")
                                .replaceAll("分", ":")
                                .replaceAll("秒", "")
                                .replaceAll("[.]", "-")
                                .replaceAll("/", "-");

                        date = DateUtils.convertFormatDate(normalized, pattern[1]);
                        break;
                    }
                }
            }
        }
        Matcher excelSerial;
        if (date == null && (excelSerial = EXCEL_SERIAL_DATE_TIME.matcher(value)).matches()) {
            // excel 45847.59512 -> 2025-07-09 14:16:58 这种格式的时间
            String dateStr;
            String timeStr;

            String beforeDecimal = excelSerial.group(1);
            String afterDecimal = excelSerial.group(2);

            if (StringUtils.isNotBlank(beforeDecimal)) {
                int dayCount = Integer.parseInt(beforeDecimal);
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.set(1899, Calendar.DECEMBER, 31);
                // Excel 错误地认为 1900 是闰年，因此从第 60 天开始要减一天
                if (dayCount >= 60) {
                    dayCount -= 1;
                }
                calendar.add(Calendar.DATE, dayCount);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateStr = sdf.format(calendar.getTime());
            } else {
                dateStr = "2025-01-01";
            }
            if (StringUtils.isNotBlank(afterDecimal)) {
                BigDecimal fraction = new BigDecimal("0." + afterDecimal);
                int secondInDay = fraction.multiply(new BigDecimal(24 * 60 * 60)).intValue();

                // 构造时间（以 1970-01-01 为基准）
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(0);
                calendar.add(Calendar.SECOND, secondInDay);

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                timeStr = sdf.format(calendar.getTime());
            } else {
                timeStr = "00:00:00";
            }

            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr + " " + timeStr);
            } catch (ParseException e) {
                throw new IllegalArgumentException("解析" + value + "时间格式异常", e);
            }
        }
        if (date == null && StringUtils.isNumeric(value)) {
            if (value.length() == 13) {
                date = new Date(Long.parseLong(value));
            } else if (value.length() == 10) {
                date = new Date(Long.parseLong(value) * 1000);
            }
        }
        return date;
    }

    static class ExcelDateFormatParser {

        /**
         * 代码来自 easy excel
         *
         * @see com.alibaba.excel.constant.BuiltinFormats
         */
        public static final Map<String, String> EXCEL_DATA_MAPPING = MapUtils.putAll(new HashMap<>(), new String[][]{
                {"yyyy/m/d", "yyyy/M/d"},
                // 15
                {"d-mmm-yy", "d-MMM-yy"},
                // 16
                {"d-mmm", "d-MMM"},
                // 17
                {"mmm-yy", "MMM-yy"},
                // 18
                {"h:mm AM/PM", "h:mm a"},
                // 19
                {"h:mm:ss AM/PM", "h:mm:ss a"},
                // 20
                {"h:mm", "H:mm"},
                // 21
                {"h:mm:ss", "H:mm:ss"},
                // 22
                // The official documentation shows "m/d/yy h:mm", but the actual test is "yyyy-m-d h:mm".
                {"yyyy-m-d h:mm", "yyyy-M-d H:mm"},
                // 23-26 No specific correspondence found in the official documentation.
                // 23
                // null,
                // 24
                // null,
                // 25
                // null,
                // 26
                // null,
                // 27
                {"yyyy\"年\"m\"月\"", "yyyy\"年\"M\"月\""},
                // 28
                {"m\"月\"d\"日\"", "M\"月\"d\"日\""},
                // 29
                {"m\"月\"d\"日\"", "M\"月\"d\"日\""},
                // 30
                {"m-d-yy", "M-d-yy"},
                // 31
                {"yyyy\"年\"m\"月\"d\"日\"", "yyyy\"年\"M\"月\"d\"日\""},
                // 32
                {"h\"时\"mm\"分\"", "H\"时\"mm\"分\""},
                // 33
                {"h\"时\"mm\"分\"ss\"秒\"", "H\"时\"mm\"分\"ss\"秒\""},
                // 34
                {"上午/下午h\"时\"mm\"分\"", "a h\"时\"mm\"分\""},
                // 35
                {"上午/下午h\"时\"mm\"分\"ss\"秒\"", "a h\"时\"mm\"分\"ss\"秒\""},
                // 36
                {"yyyy\"年\"m\"月\"", "yyyy\"年\"M\"月\""},
                // -- 无编号，附加补充 --
                {"mm:ss", "mm:ss"},
                // 46
                {"[h]:mm:ss", "H:mm:ss"}, // 简化，[h]不可直接支持
                // 47
                {"mm:ss.0", "mm:ss.S"},
                // 50
                {"yyyy\"年\"m\"月\"", "yyyy\"年\"M\"月\""},
                // 51
                {"m\"月\"d\"日\"", "M\"月\"d\"日\""},
                // 52
                {"yyyy\"年\"m\"月\"", "yyyy\"年\"M\"月\""},
                // 53
                {"m\"月\"d\"日\"", "M\"月\"d\"日\""},
                // 54
                {"m\"月\"d\"日\"", "M\"月\"d\"日\""},
                // 55
                {"上午/下午h\"时\"mm\"分\"", "a h\"时\"mm\"分\""},
                // 56
                {"上午/下午h\"时\"mm\"分\"ss\"秒\"", "a h\"时\"mm\"分\"ss\"秒\""},
                // 57
                {"yyyy\"年\"m\"月\"", "yyyy\"年\"M\"月\""},
                // 58
                {"m\"月\"d\"日\"", "M\"月\"d\"日\""}
        });

        /**
         * 将字符串解析为 java.util.Date，兼容多种 Excel 格式
         */
        public static Date parse(String value, String format) {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            if (!EXCEL_DATA_MAPPING.containsKey(format)) {
                format = normalize(format);
                value = normalize(value);
            }
            format = EXCEL_DATA_MAPPING.get(format);

            try {
                SimpleDateFormat sdf;
                if (format.contains("MMM")) {
                    Locale locale;
                    if (value.contains("月")) {
                        locale = Locale.CHINA;
                    } else {
                        locale = Locale.ENGLISH;
                    }
                    sdf = new SimpleDateFormat(format, locale);
                } else {
                    sdf = new SimpleDateFormat(format);
                }
                sdf.setLenient(true);
                return sdf.parse(value);
            } catch (Exception ignored) {
            }

            return null;
        }

        /**
         * 替换中文单位、标准化分隔符，兼容 Excel 日期格式
         */
        private static String normalize(String value) {
            if (StringUtils.isBlank(value)) {
                return value;
            }
            return value
                    .replaceAll("年", "-")
                    .replaceAll("月", "-")
                    .replaceAll("日", "")
                    .replaceAll("时", ":")
                    .replaceAll("分", ":")
                    .replaceAll("秒", "")
                    .replaceAll("上午", "AM")
                    .replaceAll("下午", "PM")
                    .replaceAll("/", "-")
                    .replaceAll("\\.", "-")
                    .replaceAll("@", " ")
                    .trim();
        }
    }
}
