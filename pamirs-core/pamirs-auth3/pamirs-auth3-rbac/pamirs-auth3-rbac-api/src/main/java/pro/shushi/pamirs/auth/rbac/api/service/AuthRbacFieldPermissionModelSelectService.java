package pro.shushi.pamirs.auth.rbac.api.service;

import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacFieldPermissionModelSelect;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

/**
 * AuthRbacFieldPermissionModelSelectService
 *
 * @author yakir on 2025/05/15 13:47.
 */
@Fun(AuthRbacFieldPermissionModelSelectService.FUN_NAMESPACE)
public interface AuthRbacFieldPermissionModelSelectService {

    String FUN_NAMESPACE = "auth.AuthRbacFieldPermissionModelSelectService";

    @Function
    Pagination<AuthRbacFieldPermissionModelSelect> queryPage(Pagination<AuthRbacFieldPermissionModelSelect> page, IWrapper<AuthRbacFieldPermissionModelSelect> queryWrapper);
}
