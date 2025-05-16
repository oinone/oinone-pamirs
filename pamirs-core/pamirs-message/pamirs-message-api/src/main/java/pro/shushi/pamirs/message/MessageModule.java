package pro.shushi.pamirs.message;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.AuthModule;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.resource.api.ResourceModule;
import pro.shushi.pamirs.user.api.UserModule;


@Component
@Module(
        name = MessageModule.MODULE_NAME,
        displayName = "消息",
        version = "5.0.0",
        dependencies = {ModuleConstants.MODULE_BASE, AuthModule.MODULE_MODULE, UserModule.MODULE_MODULE, BusinessModule.MODULE_MODULE, ResourceModule.MODULE_MODULE},
        exclusions = {}
)
@Module.module(MessageModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true)
public class MessageModule implements PamirsModule {

    public static final String MODULE_MODULE = "message";

    public static final String MODULE_NAME = "message";

    @Override
    public String[] packagePrefix() {
        return new String[]{"pro.shushi.pamirs.message"};
    }

}
