package pro.shushi.pamirs.auth.api.service.group;

import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRelResource;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

/**
 * 权限组管理
 *
 * @author Adamancy Zhang at 20:42 on 2024-06-20
 */
@Fun(AuthGroupManager.FUN_NAMESPACE)
public interface AuthGroupManager {

    String FUN_NAMESPACE = "auth.AuthGroupManager";

    @Function
    AuthGroup createResourcePermissionGroup(AuthGroup group, AuthGroupRelResource groupRelResource, AuthResourceAuthorization resourceAuthorization);

    @Function
    Boolean createGroupActionPermissions(List<AuthResourceAuthorization> resourceAuthorizations);
}
