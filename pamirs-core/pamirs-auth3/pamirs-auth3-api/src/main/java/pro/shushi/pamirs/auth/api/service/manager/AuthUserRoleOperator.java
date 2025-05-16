package pro.shushi.pamirs.auth.api.service.manager;

import pro.shushi.pamirs.auth.api.model.AuthRole;

import java.util.List;
import java.util.Set;

/**
 * 用户角色操作服务
 *
 * @author Adamancy Zhang at 17:29 on 2024-01-20
 */
public interface AuthUserRoleOperator {

    Set<Long> fetchRoleIds(Long userId);

    List<AuthRole> fetchRoles(Long userId);

    List<AuthRole> fetchActiveRoles(Long userId);
}
