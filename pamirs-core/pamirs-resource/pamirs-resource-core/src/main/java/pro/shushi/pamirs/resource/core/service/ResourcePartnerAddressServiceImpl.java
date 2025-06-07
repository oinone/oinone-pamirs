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
import pro.shushi.pamirs.resource.api.model.ResourcePartnerAddress;
import pro.shushi.pamirs.resource.api.service.ResourcePartnerAddressService;

import java.util.List;

@Fun(ResourcePartnerAddressService.FUN_NAMESPACE)
@Component
public class ResourcePartnerAddressServiceImpl implements ResourcePartnerAddressService {

    private IWrapper<ResourcePartnerAddress> initQueryWrapper(IWrapper<ResourcePartnerAddress> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ResourcePartnerAddress>) queryWrapper).from(ResourcePartnerAddress.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ResourcePartnerAddress>) queryWrapper).lambda().from(ResourcePartnerAddress.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<ResourcePartnerAddress> queryPage(Pagination<ResourcePartnerAddress> page, IWrapper<ResourcePartnerAddress> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourcePartnerAddress().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<ResourcePartnerAddress> queryListByWrapper(IWrapper<ResourcePartnerAddress> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourcePartnerAddress().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<ResourcePartnerAddress> queryListByEntity(ResourcePartnerAddress entity) {
        //entity.set(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<ResourcePartnerAddress> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public ResourcePartnerAddress queryOneByWrapper(IWrapper<ResourcePartnerAddress> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourcePartnerAddress().queryOneByWrapper(queryWrapper);
    }


    @Override
    @Function
    public ResourcePartnerAddress queryById(Long id) {
        return new ResourcePartnerAddress().queryById(id);
    }

    @Override
    @Function
    public List<ResourcePartnerAddress> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(ResourcePartnerAddress.MODEL_MODEL, ids);
    }
}
