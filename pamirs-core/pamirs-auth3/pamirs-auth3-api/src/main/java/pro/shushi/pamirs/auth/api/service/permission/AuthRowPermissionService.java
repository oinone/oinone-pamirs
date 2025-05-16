package pro.shushi.pamirs.auth.api.service.permission;

import pro.shushi.pamirs.auth.api.behavior.AuthPermissionStandardService;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 行权限服务
 *
 * @author Adamancy Zhang at 20:26 on 2024-01-06
 */
@Fun(AuthRowPermissionService.FUN_NAMESPACE)
public interface AuthRowPermissionService extends AuthPermissionStandardService<AuthRowPermission> {

    String FUN_NAMESPACE = "auth.AuthRowPermissionService";

    @Function
    @Override
    AuthRowPermission create(AuthRowPermission data);

    @Function
    @Override
    List<AuthRowPermission> createBatch(List<AuthRowPermission> list);

    @Function
    @Override
    AuthRowPermission update(AuthRowPermission data);

    @Function
    @Override
    Integer updateBatch(List<AuthRowPermission> list);

    @Function
    @Override
    Integer updateByWrapper(AuthRowPermission data, LambdaUpdateWrapper<AuthRowPermission> wrapper);

    @Function
    @Override
    AuthRowPermission createOrUpdate(AuthRowPermission data);

    @Function
    @Override
    List<AuthRowPermission> delete(List<AuthRowPermission> list);

    @Function
    @Override
    AuthRowPermission deleteOne(AuthRowPermission data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthRowPermission> wrapper);

    @Function
    @Override
    Pagination<AuthRowPermission> queryPage(Pagination<AuthRowPermission> page, LambdaQueryWrapper<AuthRowPermission> queryWrapper);

    @Function
    @Override
    AuthRowPermission queryOne(AuthRowPermission query);

    @Function
    @Override
    AuthRowPermission queryOneByWrapper(LambdaQueryWrapper<AuthRowPermission> queryWrapper);

    @Function
    @Override
    List<AuthRowPermission> queryListByWrapper(LambdaQueryWrapper<AuthRowPermission> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthRowPermission> queryWrapper);
}
