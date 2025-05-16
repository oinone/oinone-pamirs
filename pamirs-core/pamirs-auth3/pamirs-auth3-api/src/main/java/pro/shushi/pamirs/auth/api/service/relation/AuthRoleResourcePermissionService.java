package pro.shushi.pamirs.auth.api.service.relation;

import pro.shushi.pamirs.auth.api.model.relation.AuthRoleResourcePermission;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;
import java.util.Set;

/**
 * 角色-资源权限 服务
 *
 * @author Adamancy Zhang at 13:59 on 2024-01-08
 */
@Fun(AuthRoleResourcePermissionService.FUN_NAMESPACE)
public interface AuthRoleResourcePermissionService extends StandardModelService<AuthRoleResourcePermission> {

    String FUN_NAMESPACE = "auth.AuthRoleResourcePermissionService";

    @Function
    @Override
    AuthRoleResourcePermission create(AuthRoleResourcePermission data);

    @Function
    List<AuthRoleResourcePermission> createBatch(List<AuthRoleResourcePermission> list);

    @Function
    @Override
    AuthRoleResourcePermission update(AuthRoleResourcePermission data);

    @Function
    Integer updateBatch(List<AuthRoleResourcePermission> list);

    @Function
    @Override
    Integer updateByWrapper(AuthRoleResourcePermission data, LambdaUpdateWrapper<AuthRoleResourcePermission> wrapper);

    @Function
    @Override
    AuthRoleResourcePermission createOrUpdate(AuthRoleResourcePermission data);

    @Function
    @Override
    List<AuthRoleResourcePermission> delete(List<AuthRoleResourcePermission> list);

    @Function
    @Override
    AuthRoleResourcePermission deleteOne(AuthRoleResourcePermission data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthRoleResourcePermission> wrapper);

    @Function
    @Override
    Pagination<AuthRoleResourcePermission> queryPage(Pagination<AuthRoleResourcePermission> page, LambdaQueryWrapper<AuthRoleResourcePermission> queryWrapper);

    @Function
    @Override
    AuthRoleResourcePermission queryOne(AuthRoleResourcePermission query);

    @Function
    @Override
    AuthRoleResourcePermission queryOneByWrapper(LambdaQueryWrapper<AuthRoleResourcePermission> queryWrapper);

    @Function
    @Override
    List<AuthRoleResourcePermission> queryListByWrapper(LambdaQueryWrapper<AuthRoleResourcePermission> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthRoleResourcePermission> queryWrapper);

    @Function
    List<AuthRoleResourcePermission> queryListByRoleIds(Set<Long> roleIds);

    @Function
    List<AuthRoleResourcePermission> queryPermissionIdsByAllFlag();
}
