package pro.shushi.pamirs.boot.web.spi.holder;

import pro.shushi.pamirs.boot.web.spi.api.UserIdentityApi;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 用户身份标识持有者
 *
 * @author Adamancy Zhang at 10:18 on 2024-04-10
 */
public class UserIdentityHolder {

    private static final HoldKeeper<UserIdentityApi> holder = new HoldKeeper<>();

    private static UserIdentityApi get() {
        return holder.supply(() -> Spider.getDefaultExtension(UserIdentityApi.class));
    }

    public static boolean isAdmin() {
        return get().isAdmin();
    }

    public static boolean isAnonymous() {
        return get().isAnonymous();
    }
}
