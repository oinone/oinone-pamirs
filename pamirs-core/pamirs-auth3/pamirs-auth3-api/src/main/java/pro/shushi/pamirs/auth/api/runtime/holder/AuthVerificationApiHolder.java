package pro.shushi.pamirs.auth.api.runtime.holder;

import pro.shushi.pamirs.boot.web.spi.api.AuthVerificationApi;

/**
 * 权限验证API持有者
 *
 * @author Adamancy Zhang at 23:04 on 2024-04-09
 * @deprecated please using {@link pro.shushi.pamirs.boot.web.spi.holder.AuthVerificationApiHolder}
 */
@Deprecated
public class AuthVerificationApiHolder {

    public static AuthVerificationApi get() {
        return pro.shushi.pamirs.boot.web.spi.holder.AuthVerificationApiHolder.get();
    }
}
