package pro.shushi.pamirs.sequence.utils;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SequenceDateTimeFormatter
 *
 * @author yakir on 2020/04/08 17:47.
 */
@Slf4j
public class SequenceDateTimeFormatter {

    public Boolean checkDateTimeParttern(String pattern) {

        boolean vali = Boolean.FALSE;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);

        try {
            String now = LocalDateTime.now().format(fmt);
            LocalDateTime ldt = LocalDateTime.parse(now, fmt);
            vali = Boolean.TRUE;
            return vali;
        } catch (Throwable e) {
            // do nothing ...
            vali = false;
        }

        try {
            String now = LocalDate.now().format(fmt);
            LocalDate ld = LocalDate.parse(now, fmt);
            vali = Boolean.TRUE;
            return vali;
        } catch (Throwable e) {
            // do nothing ...
            vali = false;
        }

        return vali;
    }

}
