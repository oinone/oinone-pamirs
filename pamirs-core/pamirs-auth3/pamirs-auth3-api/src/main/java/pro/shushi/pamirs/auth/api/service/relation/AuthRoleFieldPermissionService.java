package pro.shushi.pamirs.auth.api.service.relation;

import pro.shushi.pamirs.auth.api.model.relation.AuthRoleFieldPermission;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;
import java.util.Set;

/**
 * 角色-字段权限 服务
 *
 * @author Adamancy Zhang at 13:55 on 2024-01-08
 */
@Fun(AuthRoleFieldPermissionService.FUN_NAMESPACE)
public interface AuthRoleFieldPermissionService extends StandardModelService<AuthRoleFieldPermission> {

    String FUN_NAMESPACE = "auth.AuthRoleFieldPermissionService";

    @Function
    @Override
    AuthRoleFieldPermission create(AuthRoleFieldPermission data);

    @Function
    List<AuthRoleFieldPermission> createBatch(List<AuthRoleFieldPermission> list);

    @Function
    @Override
    AuthRoleFieldPermission update(AuthRoleFieldPermission data);

    @Function
    Integer updateBatch(List<AuthRoleFieldPermission> list);

    @Function
    @Override
    Integer updateByWrapper(AuthRoleFieldPermission data, LambdaUpdateWrapper<AuthRoleFieldPermission> wrapper);

    @Function
    @Override
    AuthRoleFieldPermission createOrUpdate(AuthRoleFieldPermission data);

    @Function
    @Override
    List<AuthRoleFieldPermission> delete(List<AuthRoleFieldPermission> list);

    @Function
    @Override
    AuthRoleFieldPermission deleteOne(AuthRoleFieldPermission data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthRoleFieldPermission> wrapper);

    @Function
    @Override
    Pagination<AuthRoleFieldPermission> queryPage(Pagination<AuthRoleFieldPermission> page, LambdaQueryWrapper<AuthRoleFieldPermission> queryWrapper);

    @Function
    @Override
    AuthRoleFieldPermission queryOne(AuthRoleFieldPermission query);

    @Function
    @Override
    AuthRoleFieldPermission queryOneByWrapper(LambdaQueryWrapper<AuthRoleFieldPermission> queryWrapper);

    @Function
    @Override
    List<AuthRoleFieldPermission> queryListByWrapper(LambdaQueryWrapper<AuthRoleFieldPermission> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthRoleFieldPermission> queryWrapper);

    @Function
    List<AuthRoleFieldPermission> queryListByRoleIds(Set<Long> roleIds);

    @Function
    List<AuthRoleFieldPermission> queryPermissionIdsByAllFlag();
}
