package pro.shushi.pamirs.auth.api.service.manager;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.core.common.diff.DiffSet;

import java.util.List;
import java.util.Set;

/**
 * 用户角色差量服务
 *
 * @author Adamancy Zhang at 16:57 on 2024-01-20
 */
public interface AuthUserRoleDiffService {

    DiffSet<Long> saveRoles(Long userId, List<AuthRole> roles);

    void refreshUserRoles(Long userId, DiffSet<Long> diffRoleIds);

    void refreshUserRoles(Set<Long> userIds, DiffSet<Long> diffRoleIds);
}
