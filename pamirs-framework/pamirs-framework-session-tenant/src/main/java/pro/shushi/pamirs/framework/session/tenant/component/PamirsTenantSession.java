package pro.shushi.pamirs.framework.session.tenant.component;

import pro.shushi.pamirs.framework.session.tenant.api.SessionTenantApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.Optional;

/**
 * pamirs session
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 10:45 下午
 */
public class PamirsTenantSession extends PamirsSession {

    public static final String SESSION_TENANT = "TENANT";

    public static String getTenant() {
        return Optional.ofNullable(Spider.getDefaultExtension(SessionTenantApi.class).getTenant()).orElse(null);
    }

    public static void setTenant(String tenant) {
        Spider.getDefaultExtension(SessionTenantApi.class).setTenant(tenant);
    }
}
