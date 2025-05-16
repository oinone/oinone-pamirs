package pro.shushi.pamirs.auth.core.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.behavior.AuthorizedValueComputer;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleRowPermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.auth.api.service.authorize.AuthRowAuthorizeService;
import pro.shushi.pamirs.auth.core.service.authorize.AbstractAuthPermissionAuthorizeService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * DefaultAuthRowAuthorizeServiceImpl
 *
 * @author yakir on 2025/05/14 17:20.
 */
@Service
@Fun(AuthRowAuthorizeService.FUN_NAMESPACE)
public class DefaultAuthRowAuthorizeServiceImpl extends AbstractAuthPermissionAuthorizeService<RowAuthorizedValueEnum, AuthRoleRowPermission, AuthRowAuthorization> implements AuthRowAuthorizeService {


    @Function
    @Override
    public AuthRowAuthorization authorize(Long roleId, AuthRowAuthorization permission, AuthorizationSourceEnum source) {
        return null;
    }

    @Function
    @Override
    public List<AuthRowAuthorization> authorizes(Set<Long> roleIds, List<AuthRowAuthorization> permissions, AuthorizationSourceEnum source) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public AuthRowAuthorization revoke(Long roleId, AuthRowAuthorization permission) {
        return null;
    }

    @Function
    @Override
    public List<AuthRowAuthorization> revokes(Set<Long> roleIds, List<AuthRowAuthorization> permissions) {
        return Collections.emptyList();
    }

    @Override
    public AuthRowAuthorization update(Long roleId, AuthRowAuthorization permission, AuthorizationSourceEnum source) {
        return null;
    }

    @Override
    public List<AuthRowAuthorization> updates(Set<Long> roleIds, List<AuthRowAuthorization> permissions, AuthorizationSourceEnum source) {
        return Collections.emptyList();
    }

    @Override
    public List<AuthRowAuthorization> fullUpdate(Long roleId, List<AuthRowAuthorization> permissions, AuthorizationSourceEnum source) {
        return Collections.emptyList();
    }

    @Override
    public List<AuthRowAuthorization> fullUpdates(Set<Long> roleIds, List<AuthRowAuthorization> permissions, AuthorizationSourceEnum source) {
        return Collections.emptyList();
    }
}
