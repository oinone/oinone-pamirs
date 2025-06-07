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
import pro.shushi.pamirs.resource.api.model.ResourceAddress;
import pro.shushi.pamirs.resource.api.service.ResourceAddressService;

import java.util.List;

@Fun(ResourceAddressService.FUN_NAMESPACE)
@Component
public class ResourceAddressServiceImpl implements ResourceAddressService {

    private IWrapper<ResourceAddress> initQueryWrapper(IWrapper<ResourceAddress> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ResourceAddress>) queryWrapper).from(ResourceAddress.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ResourceAddress>) queryWrapper).lambda().from(ResourceAddress.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<ResourceAddress> queryPage(Pagination<ResourceAddress> page, IWrapper<ResourceAddress> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceAddress().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<ResourceAddress> queryListByWrapper(IWrapper<ResourceAddress> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceAddress().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<ResourceAddress> queryListByEntity(ResourceAddress entity) {
        //entity.set(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<ResourceAddress> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public ResourceAddress queryOneByWrapper(IWrapper<ResourceAddress> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceAddress().queryOneByWrapper(queryWrapper);
    }

    @Override
    @Function
    public ResourceAddress queryById(Long id) {
        return new ResourceAddress().queryById(id);
    }

    @Override
    @Function
    public List<ResourceAddress> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(ResourceAddress.MODEL_MODEL, ids);
    }

    @Override
    @Function
    public Integer updateById(ResourceAddress data) {
        return data.updateById();
    }

    @Override
    @Function
    public ResourceAddress create(ResourceAddress data) {
        return data.create();
    }
}
