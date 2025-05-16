package pro.shushi.pamirs.management.center.service;

import pro.shushi.pamirs.management.center.tmodel.RoleUserManager;
import pro.shushi.pamirs.management.center.tmodel.UserBindingManager;

/**
 * @author WuXin at 18:58 on 2025/1/8
 */
public interface RoleUserManagerService {

    RoleUserManager queryRoleUserById(RoleUserManager roleUserManager);

    RoleUserManager updateRoleForUser(RoleUserManager data);

    UserBindingManager bindingUser(UserBindingManager data);

    UserBindingManager queryRoleById(UserBindingManager data);
}
