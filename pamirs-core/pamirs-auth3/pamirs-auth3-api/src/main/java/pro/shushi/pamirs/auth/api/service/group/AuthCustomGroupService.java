package pro.shushi.pamirs.auth.api.service.group;

import pro.shushi.pamirs.auth.api.model.AuthCustomGroup;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 自定义权限组服务
 *
 * @author Adamancy Zhang at 12:06 on 2024-08-09
 */
@Fun(AuthCustomGroupService.FUN_NAMESPACE)
public interface AuthCustomGroupService extends StandardModelService<AuthCustomGroup> {

    String FUN_NAMESPACE = "auth.AuthCustomGroupService";

    @Function
    @Override
    AuthCustomGroup create(AuthCustomGroup data);

    @Function
    List<AuthCustomGroup> createBatch(List<AuthCustomGroup> list);

    @Function
    @Override
    AuthCustomGroup update(AuthCustomGroup data);

    @Function
    Integer updateBatch(List<AuthCustomGroup> list);

    @Function
    @Override
    Integer updateByWrapper(AuthCustomGroup data, LambdaUpdateWrapper<AuthCustomGroup> wrapper);

    @Function
    @Override
    AuthCustomGroup createOrUpdate(AuthCustomGroup data);

    @Function
    @Override
    List<AuthCustomGroup> delete(List<AuthCustomGroup> list);

    @Function
    @Override
    AuthCustomGroup deleteOne(AuthCustomGroup data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthCustomGroup> wrapper);

    @Function
    @Override
    Pagination<AuthCustomGroup> queryPage(Pagination<AuthCustomGroup> page, LambdaQueryWrapper<AuthCustomGroup> queryWrapper);

    @Function
    @Override
    AuthCustomGroup queryOne(AuthCustomGroup query);

    @Function
    @Override
    AuthCustomGroup queryOneByWrapper(LambdaQueryWrapper<AuthCustomGroup> queryWrapper);

    @Function
    @Override
    List<AuthCustomGroup> queryListByWrapper(LambdaQueryWrapper<AuthCustomGroup> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthCustomGroup> queryWrapper);
}
