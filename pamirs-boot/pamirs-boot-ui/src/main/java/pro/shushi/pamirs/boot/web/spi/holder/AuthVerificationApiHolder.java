package pro.shushi.pamirs.boot.web.spi.holder;

import pro.shushi.pamirs.boot.web.spi.api.AuthVerificationApi;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 权限验证API持有者
 *
 * @author Adamancy Zhang at 23:04 on 2024-04-09
 */
public class AuthVerificationApiHolder {

    private static final HoldKeeper<AuthVerificationApi> holdKeeper = new HoldKeeper<>();

    public static AuthVerificationApi get() {
        return holdKeeper.supply(() -> Spider.getDefaultExtension(AuthVerificationApi.class));
    }
}
