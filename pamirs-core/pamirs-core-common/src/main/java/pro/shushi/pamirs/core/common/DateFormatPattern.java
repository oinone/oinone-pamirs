package pro.shushi.pamirs.core.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Function;

/**
 * @author Adamancy Zhang on 2021-02-26 16:46
 */
public class DateFormatPattern {

    private String pattern;

    private TimeZone timeZone;

    private DateFormatPattern() {
        //reject create object
    }

    public static DateFormatPattern pattern(String pattern) {
        DateFormatPattern formatPattern = new DateFormatPattern();
        formatPattern.pattern = pattern;
        return formatPattern;
    }

    public DateFormatPattern withTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public String format(Date date) {
        return execute(dateFormat -> dateFormat.format(date));
    }

    public Date parse(String ds) throws ParseException {
        return execute(dateFormat -> {
            try {
                return dateFormat.parse(ds);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <T> T execute(Function<SimpleDateFormat, T> function) {
        String pattern = this.pattern;
        TimeZone timeZone = this.timeZone;

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setTimeZone(timeZone);
        return function.apply(dateFormat);
    }
}
