package pro.shushi.pamirs.auth.api.service;

import pro.shushi.pamirs.auth.api.model.AuthRoleType;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 角色类型服务
 *
 * @author Adamancy Zhang at 14:10 on 2024-01-04
 */
@Fun(AuthRoleTypeService.FUN_NAMESPACE)
public interface AuthRoleTypeService extends StandardModelService<AuthRoleType> {

    String FUN_NAMESPACE = "auth.AuthRoleTypeService";

    @Function
    @Override
    AuthRoleType create(AuthRoleType data);

    @Function
    @Override
    AuthRoleType update(AuthRoleType data);

    @Function
    @Override
    Integer updateByWrapper(AuthRoleType data, LambdaUpdateWrapper<AuthRoleType> wrapper);

    @Function
    @Override
    AuthRoleType createOrUpdate(AuthRoleType data);

    @Function
    @Override
    List<AuthRoleType> delete(List<AuthRoleType> list);

    @Function
    @Override
    AuthRoleType deleteOne(AuthRoleType data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthRoleType> wrapper);

    @Function
    @Override
    Pagination<AuthRoleType> queryPage(Pagination<AuthRoleType> page, LambdaQueryWrapper<AuthRoleType> queryWrapper);

    @Function
    @Override
    AuthRoleType queryOne(AuthRoleType query);

    @Function
    @Override
    AuthRoleType queryOneByWrapper(LambdaQueryWrapper<AuthRoleType> queryWrapper);

    @Function
    @Override
    List<AuthRoleType> queryListByWrapper(LambdaQueryWrapper<AuthRoleType> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthRoleType> queryWrapper);
}
