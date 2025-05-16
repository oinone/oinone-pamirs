package pro.shushi.pamirs.boot.web.spi.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.web.spi.api.UserIdentityApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 默认超级管理员服务
 *
 * @author Adamancy Zhang at 10:15 on 2024-04-10
 */
@Component
@Order
@SPI.Service
public class DefaultUserIdentityService implements UserIdentityApi {

    @Override
    public boolean isAdmin() {
        return Boolean.TRUE.equals(PamirsSession.isAdmin());
    }

    @Override
    public boolean isAnonymous() {
        return Boolean.TRUE.equals(PamirsSession.isAnonymous());
    }
}
