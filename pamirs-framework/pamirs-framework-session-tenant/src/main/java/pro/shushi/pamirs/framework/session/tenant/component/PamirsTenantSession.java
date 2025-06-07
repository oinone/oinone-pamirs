package pro.shushi.pamirs.framework.session.tenant.component;

import pro.shushi.pamirs.framework.session.tenant.api.SessionTenantApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * pamirs session
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 10:45 下午
 */
public class PamirsTenantSession extends PamirsSession {

    public static final String SESSION_TENANT = "TENANT";

    private static final HoldKeeper<SessionTenantApi> holder = new HoldKeeper<>();

    private static SessionTenantApi getApi() {
        return holder.supply(() -> Spider.getDefaultExtension(SessionTenantApi.class));
    }

    public static String getTenant() {
        return getApi().getTenant();
    }

    public static void setTenant(String tenant) {
        getApi().setTenant(tenant);
    }
}
