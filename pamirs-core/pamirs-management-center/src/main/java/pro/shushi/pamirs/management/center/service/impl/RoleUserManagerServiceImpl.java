package pro.shushi.pamirs.management.center.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.manager.AuthUserRoleDiffService;
import pro.shushi.pamirs.auth.api.service.relation.AuthUserRoleService;
import pro.shushi.pamirs.core.common.diff.DiffCollection;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.management.center.service.RoleUserManagerService;
import pro.shushi.pamirs.management.center.tmodel.RoleUserManager;
import pro.shushi.pamirs.management.center.tmodel.UserBindingManager;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WuXin at 18:58 on 2025/1/8
 */
@Service
public class RoleUserManagerServiceImpl implements RoleUserManagerService {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthUserRoleService authUserRoleService;

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthUserRoleDiffService authUserRoleDiffService;


    @Override
    public RoleUserManager queryRoleUserById(RoleUserManager roleUserManager) {
        Long roleId = roleUserManager.getRoleId();
        AuthRole authRole = validateRoleExistence(roleId);
        roleUserManager.setRoleName(authRole.getName());
        List<AuthUserRoleRel> authUserRoleRels = authUserRoleService
                .queryListByWrapper(Pops.<AuthUserRoleRel>lambdaQuery().from(AuthUserRoleRel.MODEL_MODEL).eq(AuthUserRoleRel::getRoleId, roleId));
        if (CollectionUtils.isEmpty(authUserRoleRels)) {
            return roleUserManager;
        }
        List<Long> userIds = authUserRoleRels.stream().filter(Objects::nonNull).map(AuthUserRoleRel::getUserId).collect(Collectors.toList());
        List<PamirsUser> userList = userService.queryListByIds(userIds);
        roleUserManager.setUsers(userList);
        return roleUserManager;
    }

    @Override
    public RoleUserManager updateRoleForUser(RoleUserManager data) {
        Long roleId = data.getRoleId();
        AuthRole authRole = validateRoleExistence(roleId);

        List<PamirsUser> users = data.getUsers();

        List<AuthUserRoleRel> authUserRoleRels = authUserRoleService
                .queryListByWrapper(Pops.<AuthUserRoleRel>lambdaQuery().from(AuthUserRoleRel.MODEL_MODEL).eq(AuthUserRoleRel::getRoleId, roleId));
        if (CollectionUtils.isEmpty(users) && CollectionUtils.isEmpty(authUserRoleRels)) {
            return data;
        }

        //只做添加
        if (CollectionUtils.isEmpty(authUserRoleRels)) {
            bindUserToRole(users, authRole);
            return data;
        }

        //只做删除
        if (CollectionUtils.isEmpty(users)) {
            Set<Long> userIdDbs = authUserRoleRels.stream().filter(Objects::nonNull).map(AuthUserRoleRel::getUserId).collect(Collectors.toSet());
            removeUserRole(userIdDbs, authRole);
            return data;
        }

        Set<Long> userIdDbs = authUserRoleRels.stream().filter(Objects::nonNull).map(AuthUserRoleRel::getUserId).collect(Collectors.toSet());
        Set<Long> userIds = users.stream().filter(Objects::nonNull).map(PamirsUser::getId).collect(Collectors.toSet());

        HashSet<Long> userIdAdds = new HashSet<>();
        for (Long userId : userIds) {
            if (Boolean.FALSE.equals(userIdDbs.remove(userId))) {
                userIdAdds.add(userId);
            }
        }

        //绑定用户角色
        if (CollectionUtils.isNotEmpty(userIdAdds)) {
            List<PamirsUser> pamirsUsers = userIdAdds.stream().map(i -> {
                PamirsUser pamirsUser = new PamirsUser();
                pamirsUser.setId(i);
                return pamirsUser;
            }).collect(Collectors.toList());
            bindUserToRole(pamirsUsers, authRole);
        }

        //删除用户与角色关系
        if (CollectionUtils.isNotEmpty(userIdDbs)) {
            removeUserRole(userIdDbs, authRole);
        }
        return data;
    }

    @Override
    public UserBindingManager bindingUser(UserBindingManager data) {
        userService.bindUserRole(data.getBindingUsers(), data.getRoles());
        return data;
    }

    @Override
    public UserBindingManager queryRoleById(UserBindingManager data) {
        List<Long> roleId = data.getRoles().stream().map(AuthRole::getId).collect(Collectors.toList());
        List<AuthRole> authRoles = authRoleService.queryListByWrapper(Pops.<AuthRole>lambdaQuery().from(AuthRole.MODEL_MODEL).in(AuthRole::getId, roleId));
        data.setRoles(authRoles);
        return data;
    }

    private void removeUserRole(Set<Long> userIdDbs, AuthRole authRole) {
        Integer row = authUserRoleService.deleteByWrapper(
                Pops.<AuthUserRoleRel>lambdaQuery().from(AuthUserRoleRel.MODEL_MODEL)
                        .in(AuthUserRoleRel::getUserId, userIdDbs)
                        .eq(AuthUserRoleRel::getRoleId, authRole.getId()));
        if (row != null && row > 0) {
            Set<Long> longs = new HashSet<>();
            longs.add(authRole.getId());
            authUserRoleDiffService.refreshUserRoles(userIdDbs, DiffCollection.set(null, null, null, longs));
        }
    }

    private void bindUserToRole(List<PamirsUser> users, AuthRole authRole) {
        List<AuthRole> roles = new ArrayList<>();
        roles.add(authRole);
        userService.bindUserRole(users, roles);
    }


    public AuthRole validateRoleExistence(Long roleId) {
        LambdaQueryWrapper<AuthRole> queryWrapper = Pops.<AuthRole>lambdaQuery().from(AuthRole.MODEL_MODEL).eq(AuthRole::getId, roleId);
        Long count = authRoleService.count(queryWrapper);
        if (count == null || count <= 0) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_ROLE_ERROR).errThrow();
        }
        AuthRole authRole = authRoleService.queryOneByWrapper(queryWrapper);
        if (authRole == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_ROLE_ERROR).errThrow();
        }
        return authRole;
    }
}
