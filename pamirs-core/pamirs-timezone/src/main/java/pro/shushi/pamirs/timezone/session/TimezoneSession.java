package pro.shushi.pamirs.timezone.session;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.TimeZone;

/**
 * 时区会话
 *
 * @author Adamancy Zhang at 10:52 on 2021-09-03
 */
@Slf4j
public class TimezoneSession {

    private static final String HEADER_TIMEZONE_KEY = "accept-timezone";

    private static final String SESSION_TIMEZONE = "TIMEZONE";

    public static void initTimezone() {
        if (PamirsSession.getTransmittableExtend().containsKey(SESSION_TIMEZONE)) {
            return;
        }
        PamirsSession.getTransmittableExtend().computeIfAbsent(SESSION_TIMEZONE, k -> {
            String timezoneString = PamirsSession.getRequestVariables().getHeader(HEADER_TIMEZONE_KEY);
            if (StringUtils.isBlank(timezoneString)) {
                return null;
            }
            try {
                return TimeZone.getTimeZone(timezoneString).getID();
            } catch (Exception e) {
                log.warn("无法获取当前指定时区", e);
                return null;
            }
        });
    }

    public static TimeZone getTimezone() {
        String timezoneString = PamirsSession.getTransmittableExtend().get(SESSION_TIMEZONE);
        if (timezoneString == null) {
            return null;
        }
        return TimeZone.getTimeZone(timezoneString);
    }

    public static void setTimezone(TimeZone timezone) {
        PamirsSession.getTransmittableExtend().putIfAbsent(SESSION_TIMEZONE, timezone.getID());
    }

    public static void clear() {
        PamirsSession.getTransmittableExtend().remove(SESSION_TIMEZONE);
    }
}
