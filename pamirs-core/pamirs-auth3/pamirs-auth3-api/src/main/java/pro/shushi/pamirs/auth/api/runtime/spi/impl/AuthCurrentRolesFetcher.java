package pro.shushi.pamirs.auth.api.runtime.spi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.runtime.spi.CurrentRolesFetcher;
import pro.shushi.pamirs.auth.api.service.authorize.AuthUserAuthorizeService;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Set;

/**
 * 权限模块 - 获取当前角色列表
 *
 * @author Adamancy Zhang at 11:06 on 2024-01-10
 */
@Order
@Component
@SPI.Service
public class AuthCurrentRolesFetcher implements CurrentRolesFetcher {

    @Autowired
    private AuthUserAuthorizeService userAccreditService;

    @Override
    public Set<Long> fetch() {
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            return null;
        }
        return userAccreditService.queryValidRoleIds(userId);
    }
}
