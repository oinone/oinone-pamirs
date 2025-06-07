package pro.shushi.pamirs.core.logger.keying;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * LoggerNameKeyingStrategy
 *
 * @author yakir on 2023/12/27 17:09.
 */
public class LoggerNameKeyingStrategy implements KeyingStrategy<ILoggingEvent> {

    @Override
    public String createKey(ILoggingEvent e) {
        final String loggerName;
        if (e.getLoggerName() == null) {
            loggerName = "";
        } else {
            loggerName = e.getLoggerName();
        }
        return loggerName;
    }

}
