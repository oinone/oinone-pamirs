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
import pro.shushi.pamirs.resource.api.model.ResourceDistrict;
import pro.shushi.pamirs.resource.api.service.ResourceDistrictService;

import java.util.List;

@Fun(ResourceDistrictService.FUN_NAMESPACE)
@Component
public class ResourceDistrictServiceImpl implements ResourceDistrictService {

    private IWrapper<ResourceDistrict> initQueryWrapper(IWrapper<ResourceDistrict> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ResourceDistrict>) queryWrapper).from(ResourceDistrict.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ResourceDistrict>) queryWrapper).lambda().from(ResourceDistrict.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<ResourceDistrict> queryPage(Pagination<ResourceDistrict> page, IWrapper<ResourceDistrict> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceDistrict().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<ResourceDistrict> queryListByWrapper(IWrapper<ResourceDistrict> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceDistrict().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<ResourceDistrict> queryListByEntity(ResourceDistrict entity) {
        //entity.set(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<ResourceDistrict> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public ResourceDistrict queryOneByWrapper(IWrapper<ResourceDistrict> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceDistrict().queryOneByWrapper(queryWrapper);
    }

    @Override
    @Function
    public ResourceDistrict queryByCode(String code) {
        return new ResourceDistrict().queryByCode(code);
    }

    @Override
    @Function
    public List<ResourceDistrict> queryByCodes(List<String> codes) {
        return FetchUtil.fetchListByCodes(ResourceDistrict.MODEL_MODEL, codes);
    }

    @Override
    @Function
    public ResourceDistrict queryById(Long id) {
        return new ResourceDistrict().queryById(id);
    }

    @Override
    @Function
    public List<ResourceDistrict> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(ResourceDistrict.MODEL_MODEL, ids);
    }

    @Override
    @Function
    public ResourceDistrict create(ResourceDistrict data) {
        return data.create();
    }

}
