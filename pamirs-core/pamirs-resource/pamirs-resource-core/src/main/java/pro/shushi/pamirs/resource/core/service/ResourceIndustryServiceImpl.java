package pro.shushi.pamirs.resource.core.service;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourceIndustry;
import pro.shushi.pamirs.resource.api.service.ResourceIndustryService;

import java.util.List;

@Fun(ResourceIndustryService.FUN_NAMESPACE)
@Component
public class ResourceIndustryServiceImpl implements ResourceIndustryService {

    private IWrapper<ResourceIndustry> initQueryWrapper(IWrapper<ResourceIndustry> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ResourceIndustry>) queryWrapper).from(ResourceIndustry.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ResourceIndustry>) queryWrapper).lambda().from(ResourceIndustry.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<ResourceIndustry> queryPage(Pagination<ResourceIndustry> page, IWrapper<ResourceIndustry> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceIndustry().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<ResourceIndustry> queryListByWrapper(IWrapper<ResourceIndustry> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceIndustry().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<ResourceIndustry> queryListByEntity(ResourceIndustry entity) {
        //entity.set(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<ResourceIndustry> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public ResourceIndustry queryOneByWrapper(IWrapper<ResourceIndustry> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceIndustry().queryOneByWrapper(queryWrapper);
    }

    @Override
    @Function
    public ResourceIndustry queryByCode(String code) {
        return new ResourceIndustry().queryByCode(code);
    }

    @Override
    @Function
    public List<ResourceIndustry> queryByCodes(List<String> codes) {
        return FetchUtil.fetchListByCodes(ResourceIndustry.MODEL_MODEL, codes);
    }

    @Override
    @Function
    public ResourceIndustry queryById(Long id) {
        return new ResourceIndustry().queryById(id);
    }

    @Override
    @Function
    public List<ResourceIndustry> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(ResourceIndustry.MODEL_MODEL, ids);
    }

    @Function
    @Override
    public ResourceIndustry queryByName(String name) {
        ResourceIndustry resourceIndustry1 = new ResourceIndustry().setName(name).queryOne();
        return resourceIndustry1;
    }
}
