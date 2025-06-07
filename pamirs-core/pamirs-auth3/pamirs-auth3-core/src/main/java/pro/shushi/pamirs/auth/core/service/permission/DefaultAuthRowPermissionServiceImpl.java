package pro.shushi.pamirs.auth.core.service.permission;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.auth.api.service.permission.AuthRowPermissionService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.Collections;
import java.util.List;

/**
 * DefaultAuthRowPermissionServiceImpl
 *
 * @author yakir on 2025/05/14 14:53.
 */
@Service
@Fun(AuthRowPermissionService.FUN_NAMESPACE)
public class DefaultAuthRowPermissionServiceImpl implements AuthRowPermissionService {

    @Function
    @Override
    public AuthRowPermission create(AuthRowPermission data) {
        return data;
    }

    @Function
    @Override
    public List<AuthRowPermission> createBatch(List<AuthRowPermission> list) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public AuthRowPermission update(AuthRowPermission data) {
        return data;
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthRowPermission data, LambdaUpdateWrapper<AuthRowPermission> wrapper) {
        return 0;
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthRowPermission> list) {
        return 0;
    }

    @Function
    @Override
    public AuthRowPermission createOrUpdate(AuthRowPermission data) {
        return data;
    }

    @Function
    @Override
    public List<AuthRowPermission> delete(List<AuthRowPermission> list) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public AuthRowPermission deleteOne(AuthRowPermission data) {
        return data;
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthRowPermission> wrapper) {
        return 0;
    }

    @Function
    @Override
    public Pagination<AuthRowPermission> queryPage(Pagination<AuthRowPermission> page, LambdaQueryWrapper<AuthRowPermission> queryWrapper) {
        return page;
    }

    @Function
    @Override
    public AuthRowPermission queryOne(AuthRowPermission query) {
        return query;
    }

    @Function
    @Override
    public AuthRowPermission queryOneByWrapper(LambdaQueryWrapper<AuthRowPermission> queryWrapper) {
        return null;
    }

    @Function
    @Override
    public List<AuthRowPermission> queryListByWrapper(LambdaQueryWrapper<AuthRowPermission> queryWrapper) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthRowPermission> queryWrapper) {
        return 0L;
    }
}
