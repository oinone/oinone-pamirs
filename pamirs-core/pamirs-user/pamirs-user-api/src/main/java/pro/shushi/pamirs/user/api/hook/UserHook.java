package pro.shushi.pamirs.user.api.hook;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.entry.HoldSupplier;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.user.api.login.UserInfoCache;
import pro.shushi.pamirs.user.api.spi.UserSessionApi;

@Base
@Slf4j
@Component
public class UserHook<T extends IdModel> implements HookBefore {

    private static final HoldSupplier<UserSessionApi> API = new HoldSupplier<>(() -> Spider.getDefaultExtension(UserSessionApi.class));

    @Override
    @Hook(priority = 0)
    public Object run(Function function, Object... args) {
        UserInfoCache.init();
        API.get().login(function, args);
        return null;
    }
}

