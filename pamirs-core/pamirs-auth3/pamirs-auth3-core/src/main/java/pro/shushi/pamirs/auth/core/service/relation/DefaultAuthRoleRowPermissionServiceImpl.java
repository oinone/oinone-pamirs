package pro.shushi.pamirs.auth.core.service.relation;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleRowPermission;
import pro.shushi.pamirs.auth.api.service.relation.AuthRoleRowPermissionService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * DefaultAuthRoleRowPermissionServiceImpl
 *
 * @author yakir on 2025/05/14 14:27.
 */
@Component
@Fun(AuthRoleRowPermissionService.FUN_NAMESPACE)
public class DefaultAuthRoleRowPermissionServiceImpl implements AuthRoleRowPermissionService {

    @Function
    @Override
    public AuthRoleRowPermission create(AuthRoleRowPermission data) {
        return data;
    }

    @Function
    @Override
    public List<AuthRoleRowPermission> createBatch(List<AuthRoleRowPermission> list) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public AuthRoleRowPermission update(AuthRoleRowPermission data) {
        return data;
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthRoleRowPermission data, LambdaUpdateWrapper<AuthRoleRowPermission> wrapper) {
        return 0;
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthRoleRowPermission> list) {
        return 0;
    }

    @Function
    @Override
    public AuthRoleRowPermission createOrUpdate(AuthRoleRowPermission data) {
        return data;
    }

    @Function
    @Override
    public List<AuthRoleRowPermission> delete(List<AuthRoleRowPermission> list) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public AuthRoleRowPermission deleteOne(AuthRoleRowPermission data) {
        return data;
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthRoleRowPermission> wrapper) {
        return 0;
    }

    @Function
    @Override
    public Pagination<AuthRoleRowPermission> queryPage(Pagination<AuthRoleRowPermission> page, LambdaQueryWrapper<AuthRoleRowPermission> queryWrapper) {
        return page;
    }

    @Function
    @Override
    public AuthRoleRowPermission queryOne(AuthRoleRowPermission query) {
        return query;
    }

    @Function
    @Override
    public AuthRoleRowPermission queryOneByWrapper(LambdaQueryWrapper<AuthRoleRowPermission> queryWrapper) {
        return null;
    }

    @Function
    @Override
    public List<AuthRoleRowPermission> queryListByWrapper(LambdaQueryWrapper<AuthRoleRowPermission> queryWrapper) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthRoleRowPermission> queryWrapper) {
        return 0L;
    }

    @Function
    @Override
    public List<AuthRoleRowPermission> queryListByRoleIds(Set<Long> roleIds) {
        return Collections.emptyList();
    }

    @Function
    @Override
    public List<AuthRoleRowPermission> queryPermissionIdsByAllFlag() {
        return Collections.emptyList();
    }
}
