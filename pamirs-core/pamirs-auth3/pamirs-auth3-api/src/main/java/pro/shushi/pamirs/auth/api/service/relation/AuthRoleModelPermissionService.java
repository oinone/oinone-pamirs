package pro.shushi.pamirs.auth.api.service.relation;

import pro.shushi.pamirs.auth.api.model.relation.AuthRoleModelPermission;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;
import java.util.Set;

/**
 * 角色-模型权限 服务
 *
 * @author Adamancy Zhang at 13:57 on 2024-01-08
 */
@Fun(AuthRoleModelPermissionService.FUN_NAMESPACE)
public interface AuthRoleModelPermissionService extends StandardModelService<AuthRoleModelPermission> {

    String FUN_NAMESPACE = "auth.AuthRoleModelPermissionService";

    @Function
    @Override
    AuthRoleModelPermission create(AuthRoleModelPermission data);

    @Function
    List<AuthRoleModelPermission> createBatch(List<AuthRoleModelPermission> list);

    @Function
    @Override
    AuthRoleModelPermission update(AuthRoleModelPermission data);

    @Function
    Integer updateBatch(List<AuthRoleModelPermission> list);

    @Function
    @Override
    Integer updateByWrapper(AuthRoleModelPermission data, LambdaUpdateWrapper<AuthRoleModelPermission> wrapper);

    @Function
    @Override
    AuthRoleModelPermission createOrUpdate(AuthRoleModelPermission data);

    @Function
    @Override
    List<AuthRoleModelPermission> delete(List<AuthRoleModelPermission> list);

    @Function
    @Override
    AuthRoleModelPermission deleteOne(AuthRoleModelPermission data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthRoleModelPermission> wrapper);

    @Function
    @Override
    Pagination<AuthRoleModelPermission> queryPage(Pagination<AuthRoleModelPermission> page, LambdaQueryWrapper<AuthRoleModelPermission> queryWrapper);

    @Function
    @Override
    AuthRoleModelPermission queryOne(AuthRoleModelPermission query);

    @Function
    @Override
    AuthRoleModelPermission queryOneByWrapper(LambdaQueryWrapper<AuthRoleModelPermission> queryWrapper);

    @Function
    @Override
    List<AuthRoleModelPermission> queryListByWrapper(LambdaQueryWrapper<AuthRoleModelPermission> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthRoleModelPermission> queryWrapper);

    @Function
    List<AuthRoleModelPermission> queryListByRoleIds(Set<Long> roleIds);

    @Function
    List<AuthRoleModelPermission> queryPermissionIdsByAllFlag();
}
