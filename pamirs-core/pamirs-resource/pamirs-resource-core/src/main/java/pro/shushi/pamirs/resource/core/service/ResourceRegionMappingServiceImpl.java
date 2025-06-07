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
import pro.shushi.pamirs.resource.api.model.ResourceRegionMapping;
import pro.shushi.pamirs.resource.api.service.ResourceRegionMappingService;

import java.util.List;

@Fun(ResourceRegionMappingService.FUN_NAMESPACE)
@Component
public class ResourceRegionMappingServiceImpl implements ResourceRegionMappingService {

    private IWrapper<ResourceRegionMapping> initQueryWrapper(IWrapper<ResourceRegionMapping> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ResourceRegionMapping>) queryWrapper).from(ResourceRegionMapping.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ResourceRegionMapping>) queryWrapper).lambda().from(ResourceRegionMapping.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<ResourceRegionMapping> queryPage(Pagination<ResourceRegionMapping> page, IWrapper<ResourceRegionMapping> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceRegionMapping().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<ResourceRegionMapping> queryListByWrapper(IWrapper<ResourceRegionMapping> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceRegionMapping().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<ResourceRegionMapping> queryListByEntity(ResourceRegionMapping entity) {
        //entity.set(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<ResourceRegionMapping> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public ResourceRegionMapping queryOneByWrapper(IWrapper<ResourceRegionMapping> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceRegionMapping().queryOneByWrapper(queryWrapper);
    }


    @Override
    @Function
    public ResourceRegionMapping queryById(Long id) {
        return new ResourceRegionMapping().queryById(id);
    }

    @Override
    @Function
    public List<ResourceRegionMapping> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(ResourceRegionMapping.MODEL_MODEL, ids);
    }

    @Override
    @Function
    public List<ResourceRegionMapping> createBatch(List<ResourceRegionMapping> dataList) {
        return new ResourceRegionMapping().createBatch(dataList);
    }

}
