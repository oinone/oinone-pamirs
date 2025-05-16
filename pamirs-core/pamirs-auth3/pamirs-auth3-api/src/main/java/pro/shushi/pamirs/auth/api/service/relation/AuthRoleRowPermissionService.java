package pro.shushi.pamirs.auth.api.service.relation;

import pro.shushi.pamirs.auth.api.model.relation.AuthRoleRowPermission;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;
import java.util.Set;

/**
 * 角色-行权限 服务
 *
 * @author Adamancy Zhang at 14:00 on 2024-01-08
 */
@Fun(AuthRoleRowPermissionService.FUN_NAMESPACE)
public interface AuthRoleRowPermissionService extends StandardModelService<AuthRoleRowPermission> {

    String FUN_NAMESPACE = "auth.AuthRoleRowPermissionService";

    @Function
    @Override
    AuthRoleRowPermission create(AuthRoleRowPermission data);

    @Function
    List<AuthRoleRowPermission> createBatch(List<AuthRoleRowPermission> list);

    @Function
    @Override
    AuthRoleRowPermission update(AuthRoleRowPermission data);

    @Function
    Integer updateBatch(List<AuthRoleRowPermission> list);

    @Function
    @Override
    Integer updateByWrapper(AuthRoleRowPermission data, LambdaUpdateWrapper<AuthRoleRowPermission> wrapper);

    @Function
    @Override
    AuthRoleRowPermission createOrUpdate(AuthRoleRowPermission data);

    @Function
    @Override
    List<AuthRoleRowPermission> delete(List<AuthRoleRowPermission> list);

    @Function
    @Override
    AuthRoleRowPermission deleteOne(AuthRoleRowPermission data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthRoleRowPermission> wrapper);

    @Function
    @Override
    Pagination<AuthRoleRowPermission> queryPage(Pagination<AuthRoleRowPermission> page, LambdaQueryWrapper<AuthRoleRowPermission> queryWrapper);

    @Function
    @Override
    AuthRoleRowPermission queryOne(AuthRoleRowPermission query);

    @Function
    @Override
    AuthRoleRowPermission queryOneByWrapper(LambdaQueryWrapper<AuthRoleRowPermission> queryWrapper);

    @Function
    @Override
    List<AuthRoleRowPermission> queryListByWrapper(LambdaQueryWrapper<AuthRoleRowPermission> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthRoleRowPermission> queryWrapper);

    @Function
    List<AuthRoleRowPermission> queryListByRoleIds(Set<Long> roleIds);

    @Function
    List<AuthRoleRowPermission> queryPermissionIdsByAllFlag();
}
