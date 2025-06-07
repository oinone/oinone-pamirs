package pro.shushi.pamirs.channel;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.PamirsModule;

/**
 * PamirsChannelModule
 *
 * @author yakir on 2020/04/17 21:14.
 */
@Slf4j
@Component
@Module(
        name = ChannelModule.MODULE_MODULE,
        displayName = "传输增强模型",
        version = "5.0.0"
)
@Module.module("channel")
@Module.Advanced(
        selfBuilt = true
)
public class ChannelModule implements PamirsModule {

    public static final String MODULE_MODULE = "channel";

    public static final String MODULE_NAME = "channel";

    @Override
    public String[] packagePrefix() {

        log.info("Pamirs Channel .....");

        return new String[]{
                "pro.shushi.pamirs.channel"
        };
    }

}
