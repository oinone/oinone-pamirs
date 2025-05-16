package pro.shushi.pamirs.user.core.base.util;

import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.UserLoginService;

/**
 * @author shier
 * date 2020/4/10
 */
@Slf4j
public class UserLoginHelper {

    public static ViewAction getRedirectMenu() {
        return Spider.getLoader(UserLoginService.class).getDefaultExtension().afterLoginRedirectViewAction();
    }

    public static ViewAction defaultRedirectMenu() {
        UserLoginService defaultUserLoginService = Spider.getLoader(UserLoginService.class).getDefaultExtension();
        return defaultUserLoginService.afterLoginRedirectViewAction();
    }

    public static PamirsUserTransient success(PamirsUserTransient userTransient) {
        return userTransient.setPassword(null).setConfirmPassword(null).setLogin(null).setConfirmPassword(null).setErrorCode(0);
    }

    public static PamirsUserTransient fail(PamirsUserTransient userTransient) {
        return userTransient.setPassword(null).setConfirmPassword(null).setLogin(null).setConfirmPassword(null);
    }
}
