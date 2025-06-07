package pro.shushi.pamirs.auth.api.runtime.spi.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.runtime.session.AuthRoleSession;
import pro.shushi.pamirs.auth.api.runtime.spi.FetchPermissionApi;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.boot.web.spi.holder.UserIdentityHolder;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Set;

/**
 * 默认获取权限实现
 *
 * @author Adamancy Zhang at 10:49 on 2024-01-29
 */
@Order
@Component
@SPI.Service
public class DefaultFetchPermission implements FetchPermissionApi {

    @Override
    public <R> AuthResult<R> fetch(FetchPermissions<R> fetcher) {
        if (UserIdentityHolder.isAdmin()) {
            return AuthResult.success(null);
        }
        AccessResourceInfo accessInfo = AccessResourceInfoSession.getInfo();
        if (accessInfo == null) {
            if (AccessResourceInfoSession.isEnabled()) {
                return AuthResult.error();
            } else {
                return AuthResult.success(null);
            }
        }
        Set<Long> roleIds = AuthRoleSession.getCurrentRoles();
        if (CollectionUtils.isEmpty(roleIds)) {
            return AuthResult.error();
        }
        return fetcher.apply(accessInfo, roleIds);
    }

    @Override
    public <R> AuthResult<R> fetchByRole(FetchPermissionsByRole<R> fetcher) {
        if (UserIdentityHolder.isAdmin()) {
            return AuthResult.success(null);
        }
        Set<Long> roleIds = AuthRoleSession.getCurrentRoles();
        if (CollectionUtils.isEmpty(roleIds)) {
            return AuthResult.error();
        }
        return fetcher.apply(roleIds);
    }
}
