package pro.shushi.pamirs.auth.api.service;

import pro.shushi.pamirs.auth.api.model.AuthPathMapping;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 权限路径映射服务
 *
 * @author Adamancy Zhang at 16:06 on 2024-03-25
 */
@Fun(AuthPathMappingService.FUN_NAMESPACE)
public interface AuthPathMappingService extends StandardModelService<AuthPathMapping> {

    String FUN_NAMESPACE = "auth.AuthPathMappingService";

    @Function
    @Override
    AuthPathMapping create(AuthPathMapping data);

    @Function
    List<AuthPathMapping> createBatch(List<AuthPathMapping> list);

    @Function
    @Override
    AuthPathMapping update(AuthPathMapping data);

    @Function
    Integer updateBatch(List<AuthPathMapping> list);

    @Function
    @Override
    Integer updateByWrapper(AuthPathMapping data, LambdaUpdateWrapper<AuthPathMapping> wrapper);

    @Function
    @Override
    AuthPathMapping createOrUpdate(AuthPathMapping data);

    @Function
    @Override
    List<AuthPathMapping> delete(List<AuthPathMapping> list);

    @Function
    @Override
    AuthPathMapping deleteOne(AuthPathMapping data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthPathMapping> wrapper);

    @Function
    @Override
    Pagination<AuthPathMapping> queryPage(Pagination<AuthPathMapping> page, LambdaQueryWrapper<AuthPathMapping> queryWrapper);

    @Function
    @Override
    AuthPathMapping queryOne(AuthPathMapping query);

    @Function
    @Override
    AuthPathMapping queryOneByWrapper(LambdaQueryWrapper<AuthPathMapping> queryWrapper);

    @Function
    @Override
    List<AuthPathMapping> queryListByWrapper(LambdaQueryWrapper<AuthPathMapping> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthPathMapping> queryWrapper);
}
