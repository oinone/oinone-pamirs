package pro.shushi.pamirs.core.common.test.timezone;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.timezone.TimezoneConverter;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时区转换测试
 *
 * @author Adamancy Zhang at 11:09 on 2021-09-03
 */
@DisplayName("时区转换测试")
public class TimezoneConverterTest {

    private static final ZoneId UTCZone = ZoneId.of("UTC");

    private static final ZoneId fromZone = ZoneId.of("Asia/Shanghai");

    private static final ZoneId toZone = ZoneId.of("America/New_York");

    private static final TimeZone UTC = TimeZone.getTimeZone(UTCZone);

    private static final TimeZone from = TimeZone.getTimeZone(fromZone);

    private static final TimeZone to = TimeZone.getTimeZone(toZone);

    @BeforeAll
    public static void standard() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatEnum.DATETIME.value());
        System.out.println("当前系统时间: " + dateFormat.format(now));
        dateFormat.setTimeZone(UTC);
        String utcString = dateFormat.format(now);
        System.out.println("UTC时间: " + utcString);
        dateFormat.setTimeZone(from);
        System.out.println("上海时间: " + dateFormat.format(now));
        dateFormat.setTimeZone(to);
        System.out.println("纽约时间: " + dateFormat.format(now));
        Date utc = TimezoneConverter.newInstance(from, UTC, TimezoneConverter.Feature.NEW_INSTANCE).convert(now);
        System.out.println("上海时间转UTC时间: " + utc);
        System.out.println("UTC时间转纽约时间: " + TimezoneConverter.newInstance(UTC, to, TimezoneConverter.Feature.NEW_INSTANCE).convert(utc));
        System.out.println("上海时间转纽约时间: " + TimezoneConverter.newInstance(from, to, TimezoneConverter.Feature.NEW_INSTANCE).convert(now));
    }

    @Test
    public void timestampConvertTest() {
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        System.out.println(new Date(now.getTime()));
        java.sql.Timestamp realNow = TimezoneConverter.newInstance(from, to).convert(now);
        System.out.println(new Date(realNow.getTime()));
    }

    @Test
    public void sqlDateConvertTest() {
        java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
        System.out.println(new Date(now.getTime()));
        java.sql.Date realNow = TimezoneConverter.newInstance(from, to).convert(now);
        System.out.println(new Date(realNow.getTime()));
    }

    @Test
    public void sqlTimeConvertTest() {
        java.sql.Time now = new java.sql.Time(System.currentTimeMillis());
        System.out.println(new Date(now.getTime()));
        java.sql.Time realNow = TimezoneConverter.newInstance(from, to).convert(now);
        System.out.println(new Date(realNow.getTime()));
    }

    @Test
    public void dateConvertTest() {
        Date now = new Date();
        System.out.println(now);
        Date realNow = TimezoneConverter.newInstance(from, to).convert(now);
        System.out.println(realNow);
    }

    @Test
    public void longConvertTest() {
        long now = System.currentTimeMillis();
        System.out.println(new Date(now));
        long realNow = TimezoneConverter.newInstance(from, to).convert(now);
        System.out.println(new Date(realNow));
    }
}
