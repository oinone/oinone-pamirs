package pro.shushi.pamirs.message.engine.sms;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.message.engine.IMessageEngine;
import pro.shushi.pamirs.message.model.MessageSource;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * SMSSendEngine
 *
 * @author yakir on 2019/08/22 15:43.
 */
public class SMSSendEngine implements IMessageEngine<SMSSender> {

    public static final String SENDER_NAMESPACE = SMSSender.class.getName();

    private static final Logger log = LoggerFactory.getLogger(SMSSendEngine.class);

    @Override
    public SMSSender get(@Nullable MessageSource messageSource) {
        return Spider.getLoader(SMSSender.class).getDefaultExtension();
    }
}
