package pro.shushi.pamirs.auth.api.service.permission;

import pro.shushi.pamirs.auth.api.behavior.AuthPermissionStandardService;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 字段权限服务
 *
 * @author Adamancy Zhang at 20:28 on 2024-01-06
 */
@Fun(AuthFieldPermissionService.FUN_NAMESPACE)
public interface AuthFieldPermissionService extends AuthPermissionStandardService<AuthFieldPermission> {

    String FUN_NAMESPACE = "auth.AuthFieldPermissionService";

    @Function
    @Override
    AuthFieldPermission create(AuthFieldPermission data);

    @Function
    @Override
    List<AuthFieldPermission> createBatch(List<AuthFieldPermission> list);

    @Function
    @Override
    AuthFieldPermission update(AuthFieldPermission data);

    @Function
    @Override
    Integer updateBatch(List<AuthFieldPermission> list);

    @Function
    @Override
    Integer updateByWrapper(AuthFieldPermission data, LambdaUpdateWrapper<AuthFieldPermission> wrapper);

    @Function
    @Override
    AuthFieldPermission createOrUpdate(AuthFieldPermission data);

    @Function
    @Override
    List<AuthFieldPermission> delete(List<AuthFieldPermission> list);

    @Function
    @Override
    AuthFieldPermission deleteOne(AuthFieldPermission data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthFieldPermission> wrapper);

    @Function
    @Override
    Pagination<AuthFieldPermission> queryPage(Pagination<AuthFieldPermission> page, LambdaQueryWrapper<AuthFieldPermission> queryWrapper);

    @Function
    @Override
    AuthFieldPermission queryOne(AuthFieldPermission query);

    @Function
    @Override
    AuthFieldPermission queryOneByWrapper(LambdaQueryWrapper<AuthFieldPermission> queryWrapper);

    @Function
    @Override
    List<AuthFieldPermission> queryListByWrapper(LambdaQueryWrapper<AuthFieldPermission> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthFieldPermission> queryWrapper);
}
