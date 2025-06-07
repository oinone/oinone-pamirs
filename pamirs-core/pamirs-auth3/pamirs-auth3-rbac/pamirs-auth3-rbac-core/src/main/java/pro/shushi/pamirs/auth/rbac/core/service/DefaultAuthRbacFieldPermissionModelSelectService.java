package pro.shushi.pamirs.auth.rbac.core.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacFieldPermissionModelSelect;
import pro.shushi.pamirs.auth.rbac.api.service.AuthRbacFieldPermissionModelSelectService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

/**
 * DefaultAuthRbacFieldPermissionModelSelectService
 *
 * @author yakir on 2025/05/15 13:49.
 */
@Service
@Fun(AuthRbacFieldPermissionModelSelectService.FUN_NAMESPACE)
public class DefaultAuthRbacFieldPermissionModelSelectService implements AuthRbacFieldPermissionModelSelectService {

    @Override
    @Function
    public Pagination<AuthRbacFieldPermissionModelSelect> queryPage(Pagination<AuthRbacFieldPermissionModelSelect> page, IWrapper<AuthRbacFieldPermissionModelSelect> queryWrapper) {
        return page;
    }
}
