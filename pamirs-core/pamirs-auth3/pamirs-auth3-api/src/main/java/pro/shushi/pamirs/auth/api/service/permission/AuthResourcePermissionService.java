package pro.shushi.pamirs.auth.api.service.permission;

import pro.shushi.pamirs.auth.api.behavior.AuthPermissionStandardService;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 资源权限服务
 *
 * @author Adamancy Zhang at 20:19 on 2024-01-06
 */
@Fun(AuthResourcePermissionService.FUN_NAMESPACE)
public interface AuthResourcePermissionService extends AuthPermissionStandardService<AuthResourcePermission> {

    String FUN_NAMESPACE = "auth.AuthResourcePermissionService";

    @Function
    @Override
    AuthResourcePermission create(AuthResourcePermission data);

    @Function
    @Override
    List<AuthResourcePermission> createBatch(List<AuthResourcePermission> list);

    @Function
    @Override
    AuthResourcePermission update(AuthResourcePermission data);

    @Function
    @Override
    Integer updateBatch(List<AuthResourcePermission> list);

    @Function
    @Override
    Integer updateByWrapper(AuthResourcePermission data, LambdaUpdateWrapper<AuthResourcePermission> wrapper);

    @Function
    @Override
    AuthResourcePermission createOrUpdate(AuthResourcePermission data);

    @Function
    @Override
    List<AuthResourcePermission> delete(List<AuthResourcePermission> list);

    @Function
    @Override
    AuthResourcePermission deleteOne(AuthResourcePermission data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthResourcePermission> wrapper);

    @Function
    @Override
    Pagination<AuthResourcePermission> queryPage(Pagination<AuthResourcePermission> page, LambdaQueryWrapper<AuthResourcePermission> queryWrapper);

    @Function
    @Override
    AuthResourcePermission queryOne(AuthResourcePermission query);

    @Function
    @Override
    AuthResourcePermission queryOneByWrapper(LambdaQueryWrapper<AuthResourcePermission> queryWrapper);

    @Function
    @Override
    List<AuthResourcePermission> queryListByWrapper(LambdaQueryWrapper<AuthResourcePermission> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthResourcePermission> queryWrapper);
}
