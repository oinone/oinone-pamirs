package pro.shushi.pamirs.auth.core.service.permission;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.auth.api.service.permission.AuthFieldPermissionService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.Collections;
import java.util.List;

/**
 * DefaultAuthFieldPermissionServiceImpl
 *
 * @author yakir on 2025/05/14 14:50.
 */
@Service
@Fun(AuthFieldPermissionService.FUN_NAMESPACE)
public class DefaultAuthFieldPermissionServiceImpl implements AuthFieldPermissionService {

    @Function
    @Override
    public AuthFieldPermission create(AuthFieldPermission data) {
        return data;
    }

    @Function
    @Override
    public List<AuthFieldPermission> createBatch(List<AuthFieldPermission> list) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public AuthFieldPermission update(AuthFieldPermission data) {
        return data;
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthFieldPermission data, LambdaUpdateWrapper<AuthFieldPermission> wrapper) {
        return 0;
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthFieldPermission> list) {
        return 0;
    }

    @Function
    @Override
    public AuthFieldPermission createOrUpdate(AuthFieldPermission data) {
        return data;
    }

    @Function
    @Override
    public List<AuthFieldPermission> delete(List<AuthFieldPermission> list) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public AuthFieldPermission deleteOne(AuthFieldPermission data) {
        return data;
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthFieldPermission> wrapper) {
        return 0;
    }

    @Function
    @Override
    public Pagination<AuthFieldPermission> queryPage(Pagination<AuthFieldPermission> page, LambdaQueryWrapper<AuthFieldPermission> queryWrapper) {
        return page;
    }

    @Function
    @Override
    public AuthFieldPermission queryOne(AuthFieldPermission query) {
        return query;
    }

    @Function
    @Override
    public AuthFieldPermission queryOneByWrapper(LambdaQueryWrapper<AuthFieldPermission> queryWrapper) {
        return null;
    }

    @Function
    @Override
    public List<AuthFieldPermission> queryListByWrapper(LambdaQueryWrapper<AuthFieldPermission> queryWrapper) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthFieldPermission> queryWrapper) {
        return 0L;
    }

}
