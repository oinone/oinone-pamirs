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
import pro.shushi.pamirs.resource.api.model.ResourceCity;
import pro.shushi.pamirs.resource.api.service.ResourceCityService;

import java.util.List;

@Fun(ResourceCityService.FUN_NAMESPACE)
@Component
public class ResourceCityServiceImpl implements ResourceCityService {

    private IWrapper<ResourceCity> initQueryWrapper(IWrapper<ResourceCity> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ResourceCity>) queryWrapper).from(ResourceCity.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ResourceCity>) queryWrapper).lambda().from(ResourceCity.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<ResourceCity> queryPage(Pagination<ResourceCity> page, IWrapper<ResourceCity> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceCity().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<ResourceCity> queryListByWrapper(IWrapper<ResourceCity> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceCity().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<ResourceCity> queryListByEntity(ResourceCity entity) {
        //entity.set(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<ResourceCity> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public ResourceCity queryOneByWrapper(IWrapper<ResourceCity> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceCity().queryOneByWrapper(queryWrapper);
    }

    @Override
    @Function
    public ResourceCity queryByCode(String code) {
        return new ResourceCity().queryByCode(code);
    }

    @Override
    @Function
    public List<ResourceCity> queryByCodes(List<String> codes) {
        return FetchUtil.fetchListByCodes(ResourceCity.MODEL_MODEL, codes);
    }

    @Override
    @Function
    public ResourceCity queryById(Long id) {
        return new ResourceCity().queryById(id);
    }

    @Override
    @Function
    public List<ResourceCity> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(ResourceCity.MODEL_MODEL, ids);
    }

    @Override
    @Function
    public ResourceCity queryByName(String name) {
        return new ResourceCity().setName(name).queryOne();
    }
}
