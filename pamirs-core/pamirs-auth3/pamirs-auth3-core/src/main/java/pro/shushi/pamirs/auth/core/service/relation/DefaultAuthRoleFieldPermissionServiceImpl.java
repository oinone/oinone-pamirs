package pro.shushi.pamirs.auth.core.service.relation;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleFieldPermission;
import pro.shushi.pamirs.auth.api.service.relation.AuthRoleFieldPermissionService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * DefaultAuthRoleFieldPermissionServiceImpl
 *
 * @author yakir on 2025/05/14 14:31.
 */
@Service
@Fun(AuthRoleFieldPermissionService.FUN_NAMESPACE)
public class DefaultAuthRoleFieldPermissionServiceImpl implements AuthRoleFieldPermissionService {

    @Function
    @Override
    public AuthRoleFieldPermission create(AuthRoleFieldPermission data) {
        return data;
    }

    @Function
    @Override
    public List<AuthRoleFieldPermission> createBatch(List<AuthRoleFieldPermission> list) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public AuthRoleFieldPermission update(AuthRoleFieldPermission data) {
        return data;
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthRoleFieldPermission data, LambdaUpdateWrapper<AuthRoleFieldPermission> wrapper) {
        return 0;
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthRoleFieldPermission> list) {
        return 0;
    }

    @Function
    @Override
    public AuthRoleFieldPermission createOrUpdate(AuthRoleFieldPermission data) {
        return data;
    }

    @Function
    @Override
    public List<AuthRoleFieldPermission> delete(List<AuthRoleFieldPermission> list) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public AuthRoleFieldPermission deleteOne(AuthRoleFieldPermission data) {
        return data;
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthRoleFieldPermission> wrapper) {
        return 0;
    }

    @Function
    @Override
    public Pagination<AuthRoleFieldPermission> queryPage(Pagination<AuthRoleFieldPermission> page, LambdaQueryWrapper<AuthRoleFieldPermission> queryWrapper) {
        return page;
    }

    @Function
    @Override
    public AuthRoleFieldPermission queryOne(AuthRoleFieldPermission query) {
        return query;
    }

    @Function
    @Override
    public AuthRoleFieldPermission queryOneByWrapper(LambdaQueryWrapper<AuthRoleFieldPermission> queryWrapper) {
        return null;
    }

    @Function
    @Override
    public List<AuthRoleFieldPermission> queryListByWrapper(LambdaQueryWrapper<AuthRoleFieldPermission> queryWrapper) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthRoleFieldPermission> queryWrapper) {
        return 0L;
    }

    @Function
    @Override
    public List<AuthRoleFieldPermission> queryListByRoleIds(Set<Long> roleIds) {
        return Collections.emptyList();
    }

    @Override
    public List<AuthRoleFieldPermission> queryPermissionIdsByAllFlag() {
        return Collections.emptyList();
    }

}
