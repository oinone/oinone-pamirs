package pro.shushi.pamirs.auth.api.runtime.spi.defaults;

import com.google.common.collect.Sets;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.SystemRole;
import pro.shushi.pamirs.auth.api.runtime.spi.CurrentRolesCacheApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Set;

/**
 * 默认管理员角色
 *
 * @author Adamancy Zhang at 21:08 on 2026-02-27
 */
@Order
@Component
@SPI.Service
public class DefaultAdminRoleApi implements CurrentRolesCacheApi {

    @Override
    public Set<Long> get() {
        return Sets.newHashSet(SystemRole.admin().getId());
    }

    @Override
    public void set(Set<Long> roleIds) {
        // do nothing.
    }
}
