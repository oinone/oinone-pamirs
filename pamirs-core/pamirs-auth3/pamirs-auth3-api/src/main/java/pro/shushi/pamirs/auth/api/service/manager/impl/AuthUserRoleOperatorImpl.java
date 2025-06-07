package pro.shushi.pamirs.auth.api.service.manager.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.manager.AuthUserRoleOperator;
import pro.shushi.pamirs.auth.api.service.relation.AuthUserRoleService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Adamancy Zhang at 17:30 on 2024-01-20
 */
@Service
public class AuthUserRoleOperatorImpl implements AuthUserRoleOperator {

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthUserRoleService authUserRoleService;

    @Override
    public Set<Long> fetchRoleIds(Long userId) {
        List<AuthUserRoleRel> authUserRoleRels = authUserRoleService.queryListByWrapper(Pops.<AuthUserRoleRel>lambdaQuery()
                .from(AuthUserRoleRel.MODEL_MODEL)
                .eq(AuthUserRoleRel::getUserId, userId));
        if (CollectionUtils.isEmpty(authUserRoleRels)) {
            return Collections.emptySet();
        }
        return authUserRoleRels.stream().map(AuthUserRoleRel::getRoleId).collect(Collectors.toSet());
    }

    @Override
    public List<AuthRole> fetchRoles(Long userId) {
        return fetchRoles(userId, false);
    }

    @Override
    public List<AuthRole> fetchActiveRoles(Long userId) {
        return fetchRoles(userId, true);
    }

    private List<AuthRole> fetchRoles(Long userId, boolean isOnlyActive) {
        Set<Long> roleIds = fetchRoleIds(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        if (isOnlyActive) {
            return authRoleService.fetchActiveRoles(roleIds);
        }
        return authRoleService.fetchRoles(roleIds);
    }
}
