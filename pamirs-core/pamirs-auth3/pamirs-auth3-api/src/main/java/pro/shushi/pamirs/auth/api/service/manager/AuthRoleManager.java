package pro.shushi.pamirs.auth.api.service.manager;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

/**
 * 角色管理服务
 *
 * @author Adamancy Zhang at 14:38 on 2024-01-09
 */
@Fun(AuthRoleManager.FUN_NAMESPACE)
public interface AuthRoleManager {

    String FUN_NAMESPACE = "auth.AuthRoleManager";

    @Function
    Boolean delete(List<AuthRole> roles);

    @Function
    Boolean active(AuthRole role);

    @Function
    Boolean disable(AuthRole role);
}
