package pro.shushi.pamirs.auth.api.service;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.tmodel.RoleQueryFilter;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;
import java.util.Set;

/**
 * 角色服务
 *
 * @author Adamancy Zhang at 14:10 on 2024-01-04
 */
@Fun(AuthRoleService.FUN_NAMESPACE)
public interface AuthRoleService extends StandardModelService<AuthRole> {

    String FUN_NAMESPACE = "auth.AuthRoleService";

    @Function
    @Override
    AuthRole create(AuthRole data);

    @Function
    List<AuthRole> createBatch(List<AuthRole> list);

    @Function
    @Override
    AuthRole update(AuthRole data);

    @Function
    Integer updateBatch(List<AuthRole> list);

    @Function
    @Override
    Integer updateByWrapper(AuthRole data, LambdaUpdateWrapper<AuthRole> wrapper);

    @Function
    @Override
    AuthRole createOrUpdate(AuthRole data);

    @Function
    @Override
    List<AuthRole> delete(List<AuthRole> list);

    @Function
    @Override
    AuthRole deleteOne(AuthRole data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthRole> wrapper);

    @Function
    @Override
    Pagination<AuthRole> queryPage(Pagination<AuthRole> page, LambdaQueryWrapper<AuthRole> queryWrapper);

    @Function
    @Override
    AuthRole queryOne(AuthRole query);

    @Function
    @Override
    AuthRole queryOneByWrapper(LambdaQueryWrapper<AuthRole> queryWrapper);

    @Function
    @Override
    List<AuthRole> queryListByWrapper(LambdaQueryWrapper<AuthRole> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthRole> queryWrapper);

    @Function
    List<AuthRole> fetchRoles(Set<Long> ids);

    @Function
    List<AuthRole> fetchActiveRoles(Set<Long> ids);

    @Function
    Boolean active(Long id);

    @Function
    Boolean disable(Long id);

    @Function
    List<AuthRole> queryListByFilter(RoleQueryFilter query);
}
