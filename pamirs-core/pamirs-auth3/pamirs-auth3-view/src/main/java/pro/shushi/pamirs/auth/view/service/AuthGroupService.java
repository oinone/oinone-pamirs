package pro.shushi.pamirs.auth.view.service;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.view.pmodel.AuthGroupSystemPermissionProxy;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;
import java.util.Set;

/**
 * 权限组数据服务
 *
 * @author Adamancy Zhang at 11:02 on 2024-01-17
 */
@Fun(AuthGroupService.FUN_NAMESPACE)
public interface AuthGroupService {

    String FUN_NAMESPACE = "auth.AuthGroupService";

    @Function
    Boolean verifyIsManagement(AuthGroupSystemPermissionProxy data);

    @Function
    AuthGroupSystemPermissionProxy createSystemPermissionGroup(AuthGroupSystemPermissionProxy data);

    @Function
    AuthGroupSystemPermissionProxy updateSystemPermissionGroup(AuthGroupSystemPermissionProxy data);

    @Function
    Boolean delete(Long groupId);

    @Function
    Boolean active(Long groupId);

    @Function
    Boolean isActivated(Long groupId);

    @Function
    Boolean disable(Long groupId);

    @Function
    List<AuthRole> modifyRoles(Long groupId, Set<Long> roleIds);
}
