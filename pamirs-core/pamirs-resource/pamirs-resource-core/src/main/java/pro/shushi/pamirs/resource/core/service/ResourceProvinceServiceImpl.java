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
import pro.shushi.pamirs.resource.api.model.ResourceProvince;
import pro.shushi.pamirs.resource.api.service.ResourceProvinceService;

import java.util.List;

@Fun(ResourceProvinceService.FUN_NAMESPACE)
@Component
public class ResourceProvinceServiceImpl implements ResourceProvinceService {

    private IWrapper<ResourceProvince> initQueryWrapper(IWrapper<ResourceProvince> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ResourceProvince>) queryWrapper).from(ResourceProvince.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ResourceProvince>) queryWrapper).lambda().from(ResourceProvince.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<ResourceProvince> queryPage(Pagination<ResourceProvince> page, IWrapper<ResourceProvince> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceProvince().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<ResourceProvince> queryListByWrapper(IWrapper<ResourceProvince> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceProvince().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<ResourceProvince> queryListByEntity(ResourceProvince entity) {
        //entity.set(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<ResourceProvince> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public ResourceProvince queryOneByWrapper(IWrapper<ResourceProvince> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceProvince().queryOneByWrapper(queryWrapper);
    }

    @Override
    @Function
    public ResourceProvince queryByCode(String code) {
        return new ResourceProvince().queryByCode(code);
    }

    @Override
    @Function
    public List<ResourceProvince> queryByCodes(List<String> codes) {
        return FetchUtil.fetchListByCodes(ResourceProvince.MODEL_MODEL, codes);
    }

    @Override
    @Function
    public ResourceProvince queryById(Long id) {
        return new ResourceProvince().queryById(id);
    }

    @Override
    @Function
    public List<ResourceProvince> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(ResourceProvince.MODEL_MODEL, ids);
    }

    @Function
    @Override
    public ResourceProvince queryByName(String name) {
        return new ResourceProvince().setName(name).queryOne();
    }
}
