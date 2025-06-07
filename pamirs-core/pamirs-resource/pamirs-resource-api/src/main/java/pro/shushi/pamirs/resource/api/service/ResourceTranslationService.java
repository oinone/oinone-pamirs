package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * {@link ResourceTranslation}服务
 *
 * @author Adamancy Zhang at 14:54 on 2021-09-26
 */
@Fun(ResourceTranslationService.FUN_NAMESPACE)
public interface ResourceTranslationService extends StandardModelService<ResourceTranslation> {

    String FUN_NAMESPACE = "resource.ResourceTranslationService";

    @Function
    @Nullable
    @Override
    ResourceTranslation create(ResourceTranslation data);

    @Function
    @Nullable
    @Override
    ResourceTranslation update(ResourceTranslation data);

    @Function
    @Override
    Integer updateByWrapper(ResourceTranslation data, LambdaUpdateWrapper<ResourceTranslation> wrapper);

    @Function
    @Override
    List<ResourceTranslation> delete(List<ResourceTranslation> list);

    @Function
    @Nullable
    @Override
    ResourceTranslation deleteOne(ResourceTranslation data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<ResourceTranslation> wrapper);

    @Function
    @Override
    Pagination<ResourceTranslation> queryPage(Pagination<ResourceTranslation> page, LambdaQueryWrapper<ResourceTranslation> queryWrapper);

    @Function
    @Nullable
    @Override
    ResourceTranslation queryOne(ResourceTranslation query);

    @Function
    @Nullable
    @Override
    ResourceTranslation queryOneByWrapper(LambdaQueryWrapper<ResourceTranslation> queryWrapper);

    @Function
    @Override
    List<ResourceTranslation> queryListByWrapper(LambdaQueryWrapper<ResourceTranslation> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<ResourceTranslation> queryWrapper);
}
