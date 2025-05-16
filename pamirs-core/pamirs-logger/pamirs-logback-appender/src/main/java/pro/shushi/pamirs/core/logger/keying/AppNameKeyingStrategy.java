package pro.shushi.pamirs.core.logger.keying;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.spi.ContextAwareBase;

/**
 * AppNameKeyingStrategy
 *
 * @author yakir on 2023/12/28 09:59.
 */
public class AppNameKeyingStrategy extends ContextAwareBase implements KeyingStrategy<ILoggingEvent> {

    public String appName;

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        if (appName == null) {
            appName = context.getProperty(CoreConstants.CONTEXT_NAME_KEY);
        } else if ("app.name_IS_UNDEFINED".equalsIgnoreCase(appName)) {
            appName = "default";
        }
    }

    @Override
    public String createKey(ILoggingEvent iLoggingEvent) {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
