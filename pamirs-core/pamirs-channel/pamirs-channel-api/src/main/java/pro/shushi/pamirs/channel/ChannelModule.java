package pro.shushi.pamirs.channel;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxHomepage;
import pro.shushi.pamirs.channel.model.ChannelModel;
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
        name = ChannelModule.MODULE_NAME,
        displayName = "传输增强模型",
        version = "5.0.0"
)
@Module.module(ChannelModule.MODULE_MODULE)
@Module.Advanced(
        selfBuilt = true
)
@UxHomepage(actionName = "ChannelMenus_ChannelModelMenu", value = @UxRoute(ChannelModel.MODEL_MODEL))
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
