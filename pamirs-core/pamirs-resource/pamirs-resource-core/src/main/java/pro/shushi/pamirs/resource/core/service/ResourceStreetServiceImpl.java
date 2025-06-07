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
import pro.shushi.pamirs.resource.api.model.ResourceStreet;
import pro.shushi.pamirs.resource.api.service.ResourceStreetService;

import java.util.List;

@Fun(ResourceStreetService.FUN_NAMESPACE)
@Component
public class ResourceStreetServiceImpl implements ResourceStreetService {

    private IWrapper<ResourceStreet> initQueryWrapper(IWrapper<ResourceStreet> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ResourceStreet>) queryWrapper).from(ResourceStreet.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ResourceStreet>) queryWrapper).lambda().from(ResourceStreet.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<ResourceStreet> queryPage(Pagination<ResourceStreet> page, IWrapper<ResourceStreet> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceStreet().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<ResourceStreet> queryListByWrapper(IWrapper<ResourceStreet> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceStreet().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<ResourceStreet> queryListByEntity(ResourceStreet entity) {
        //entity.set(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<ResourceStreet> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public ResourceStreet queryOneByWrapper(IWrapper<ResourceStreet> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceStreet().queryOneByWrapper(queryWrapper);
    }

    @Override
    @Function
    public ResourceStreet queryByCode(String code) {
        return new ResourceStreet().queryByCode(code);
    }

    @Override
    @Function
    public List<ResourceStreet> queryByCodes(List<String> codes) {
        return FetchUtil.fetchListByCodes(ResourceStreet.MODEL_MODEL, codes);
    }

    @Override
    @Function
    public ResourceStreet queryById(Long id) {
        return new ResourceStreet().queryById(id);
    }

    @Override
    @Function
    public List<ResourceStreet> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(ResourceStreet.MODEL_MODEL, ids);
    }

    @Override
    @Function
    public ResourceStreet create(ResourceStreet data) {
        return data.create();
    }
}
