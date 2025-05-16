package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

/**
 * 简单用户服务
 *
 * @author Adamancy Zhang at 17:27 on 2024-01-12
 */
@Fun(UserSimpleService.FUN_NAMESPACE)
public interface UserSimpleService extends StandardModelService<PamirsUser> {

    String FUN_NAMESPACE = "user.UserSimpleService";

    @Function
    @Override
    PamirsUser create(PamirsUser data);

    @Function
    List<PamirsUser> createBatch(List<PamirsUser> list);

    @Function
    @Override
    PamirsUser update(PamirsUser data);

    @Function
    Integer updateBatch(List<PamirsUser> list);

    @Function
    @Override
    Integer updateByWrapper(PamirsUser data, LambdaUpdateWrapper<PamirsUser> wrapper);

    @Function
    @Override
    PamirsUser createOrUpdate(PamirsUser data);

    @Function
    @Override
    List<PamirsUser> delete(List<PamirsUser> list);

    @Function
    @Override
    PamirsUser deleteOne(PamirsUser data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<PamirsUser> wrapper);

    @Function
    @Override
    Pagination<PamirsUser> queryPage(Pagination<PamirsUser> page, LambdaQueryWrapper<PamirsUser> queryWrapper);

    @Function
    @Override
    PamirsUser queryOne(PamirsUser query);

    @Function
    @Override
    PamirsUser queryOneByWrapper(LambdaQueryWrapper<PamirsUser> queryWrapper);

    @Function
    @Override
    List<PamirsUser> queryListByWrapper(LambdaQueryWrapper<PamirsUser> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<PamirsUser> queryWrapper);
}
