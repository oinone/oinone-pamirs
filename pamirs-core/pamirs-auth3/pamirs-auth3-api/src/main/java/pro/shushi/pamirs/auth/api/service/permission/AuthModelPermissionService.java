package pro.shushi.pamirs.auth.api.service.permission;

import pro.shushi.pamirs.auth.api.behavior.AuthPermissionStandardService;
import pro.shushi.pamirs.auth.api.model.permission.AuthModelPermission;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 模型权限项服务
 *
 * @author Adamancy Zhang at 20:29 on 2024-01-06
 */
@Fun(AuthModelPermissionService.FUN_NAMESPACE)
public interface AuthModelPermissionService extends AuthPermissionStandardService<AuthModelPermission> {

    String FUN_NAMESPACE = "auth.AuthModelPermissionService";

    @Function
    @Override
    AuthModelPermission create(AuthModelPermission data);

    @Function
    @Override
    List<AuthModelPermission> createBatch(List<AuthModelPermission> list);

    @Function
    @Override
    AuthModelPermission update(AuthModelPermission data);

    @Function
    @Override
    Integer updateBatch(List<AuthModelPermission> list);

    @Function
    @Override
    Integer updateByWrapper(AuthModelPermission data, LambdaUpdateWrapper<AuthModelPermission> wrapper);

    @Function
    @Override
    AuthModelPermission createOrUpdate(AuthModelPermission data);

    @Function
    @Override
    List<AuthModelPermission> delete(List<AuthModelPermission> list);

    @Function
    @Override
    AuthModelPermission deleteOne(AuthModelPermission data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthModelPermission> wrapper);

    @Function
    @Override
    Pagination<AuthModelPermission> queryPage(Pagination<AuthModelPermission> page, LambdaQueryWrapper<AuthModelPermission> queryWrapper);

    @Function
    @Override
    AuthModelPermission queryOne(AuthModelPermission query);

    @Function
    @Override
    AuthModelPermission queryOneByWrapper(LambdaQueryWrapper<AuthModelPermission> queryWrapper);

    @Function
    @Override
    List<AuthModelPermission> queryListByWrapper(LambdaQueryWrapper<AuthModelPermission> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthModelPermission> queryWrapper);
}
