package pro.shushi.pamirs.auth.api.service.relation;

import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;
import java.util.Set;

/**
 * 用户-角色 服务
 *
 * @author Adamancy Zhang at 12:41 on 2024-01-08
 */
@Fun(AuthUserRoleService.FUN_NAMESPACE)
public interface AuthUserRoleService extends StandardModelService<AuthUserRoleRel> {

    String FUN_NAMESPACE = "auth.AuthUserRoleService";

    @Function
    @Override
    AuthUserRoleRel create(AuthUserRoleRel data);

    @Function
    List<AuthUserRoleRel> createBatch(List<AuthUserRoleRel> list);

    @Function
    @Override
    Integer updateByWrapper(AuthUserRoleRel data, LambdaUpdateWrapper<AuthUserRoleRel> wrapper);

    @Function
    @Override
    AuthUserRoleRel createOrUpdate(AuthUserRoleRel data);

    @Function
    @Override
    List<AuthUserRoleRel> delete(List<AuthUserRoleRel> list);

    @Function
    @Override
    AuthUserRoleRel deleteOne(AuthUserRoleRel data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthUserRoleRel> wrapper);

    @Function
    @Override
    Pagination<AuthUserRoleRel> queryPage(Pagination<AuthUserRoleRel> page, LambdaQueryWrapper<AuthUserRoleRel> queryWrapper);

    @Function
    @Override
    AuthUserRoleRel queryOne(AuthUserRoleRel query);

    @Function
    @Override
    AuthUserRoleRel queryOneByWrapper(LambdaQueryWrapper<AuthUserRoleRel> queryWrapper);

    @Function
    @Override
    List<AuthUserRoleRel> queryListByWrapper(LambdaQueryWrapper<AuthUserRoleRel> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthUserRoleRel> queryWrapper);

    @Function
    List<AuthUserRoleRel> queryListByUserIds(Set<Long> userIds);

    @Function
    List<AuthUserRoleRel> queryValidListByUserIds(Set<Long> userIds);

    @Function
    List<AuthUserRoleRel> queryListByRoleIds(Set<Long> roleIds);

    @Function
    List<AuthUserRoleRel> queryValidListByRoleIds(Set<Long> roleIds);

    @Function
    List<AuthUserRoleRel> queryRolesByAllFlag();
}
