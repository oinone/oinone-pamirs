package pro.shushi.pamirs.framework.session.tenant.component;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.session.tenant.api.SessionTenantApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * @author Adamancy Zhang on 2021-04-20 20:28
 */
@Order
@Component
@SPI.Service
public class DefaultSessionTenant implements SessionTenantApi {

    @Override
    public void setTenant(String tenant) {
    }
}
