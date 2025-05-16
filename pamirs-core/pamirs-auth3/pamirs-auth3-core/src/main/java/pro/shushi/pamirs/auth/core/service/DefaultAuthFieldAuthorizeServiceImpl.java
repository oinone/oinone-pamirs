package pro.shushi.pamirs.auth.core.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleFieldPermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.service.authorize.AuthFieldAuthorizeService;
import pro.shushi.pamirs.auth.core.service.authorize.AbstractAuthPermissionAuthorizeService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * DefaultAuthFieldAuthorizeServiceImpl
 *
 * @author yakir on 2025/05/14 17:16.
 */
@Service
@Fun(AuthFieldAuthorizeService.FUN_NAMESPACE)
public class DefaultAuthFieldAuthorizeServiceImpl extends AbstractAuthPermissionAuthorizeService<FieldAuthorizedValueEnum, AuthRoleFieldPermission, AuthFieldAuthorization> implements AuthFieldAuthorizeService {

    @Function
    @Override
    public AuthFieldAuthorization authorize(Long roleId, AuthFieldAuthorization permission, AuthorizationSourceEnum source) {
        return null;
    }

    @Function
    @Override
    public List<AuthFieldAuthorization> authorizes(Set<Long> roleIds, List<AuthFieldAuthorization> permissions, AuthorizationSourceEnum source) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public AuthFieldAuthorization revoke(Long roleId, AuthFieldAuthorization permission) {
        return null;
    }

    @Function
    @Override
    public List<AuthFieldAuthorization> revokes(Set<Long> roleIds, List<AuthFieldAuthorization> permissions) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public AuthFieldAuthorization update(Long roleId, AuthFieldAuthorization permission, AuthorizationSourceEnum source) {
        return null;
    }

    @Function
    @Override
    public List<AuthFieldAuthorization> updates(Set<Long> roleIds, List<AuthFieldAuthorization> permissions, AuthorizationSourceEnum source) {
        return Collections.emptyList();
    }
}
