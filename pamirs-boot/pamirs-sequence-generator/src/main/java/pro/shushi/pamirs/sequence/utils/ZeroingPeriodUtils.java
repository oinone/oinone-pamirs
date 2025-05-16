package pro.shushi.pamirs.sequence.utils;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.enmu.SequenceEnum;
import pro.shushi.pamirs.meta.enmu.TimePeriodEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author drome
 * @date 2021/8/124:30 下午
 */
public class ZeroingPeriodUtils {

    public static final String PERIOD_SEPARATOR = "_PERIOD_";

    private static final DateTimeFormatter FORMATTER_YEAR = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter FORMATTER_MONTH = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter FORMATTER_DAY = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 序列生成方式是否涉及时间. 涉及时间才应用归零周期
     *
     * @param sequence {@link pro.shushi.pamirs.meta.enmu.SequenceEnum}
     * @return boolean
     */
    public static boolean isDateSequence(String sequence) {
        return SequenceEnum.DATE_SEQ.value().equals(sequence) || SequenceEnum.DATE_ORDERLY_SEQ.value().equals(sequence);
    }


    /**
     * 当前时间对应归零周期类型的字符串
     *
     * @param zeroingPeriod {@link TimePeriodEnum}
     * @return String
     */
    public static String periodFormat(TimePeriodEnum zeroingPeriod) {
        return periodFormat(zeroingPeriod, LocalDate.now());
    }

    public static String periodFormat(TimePeriodEnum zeroingPeriod, LocalDate date) {
        if (zeroingPeriod == null) {
            return null;
        }
        return getZeroingPeriodFormatter(zeroingPeriod).format(date);
    }

    public static String periodFormat(TimePeriodEnum zeroingPeriod, LocalDateTime dateTime) {
        if (zeroingPeriod == null) {
            return null;
        }
        return getZeroingPeriodFormatter(zeroingPeriod).format(dateTime);
    }

    private static DateTimeFormatter getZeroingPeriodFormatter(TimePeriodEnum zeroingPeriod) {
        DateTimeFormatter formatter;
        switch (zeroingPeriod) {
            case YEAR:
                formatter = FORMATTER_YEAR;
                break;
            case MONTH:
                formatter = FORMATTER_MONTH;
                break;
            case DAY:
                formatter = FORMATTER_DAY;
                break;
            default:
                throw new IllegalArgumentException("Invalid zeroing period value. value=" + zeroingPeriod);
        }
        return formatter;
    }

    public static String buildLeafAllocCode(String code, String period) {
        if (StringUtils.isBlank(period)) {
            return code;
        }
        return code + ZeroingPeriodUtils.PERIOD_SEPARATOR + period;
    }

}
