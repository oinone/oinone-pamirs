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
import pro.shushi.pamirs.resource.api.model.ResourceCurrency;
import pro.shushi.pamirs.resource.api.service.ResourceCurrencyService;

import java.util.List;

@Fun(ResourceCurrencyService.FUN_NAMESPACE)
@Component
public class ResourceCurrencyServiceImpl implements ResourceCurrencyService {

    private IWrapper<ResourceCurrency> initQueryWrapper(IWrapper<ResourceCurrency> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ResourceCurrency>) queryWrapper).from(ResourceCurrency.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ResourceCurrency>) queryWrapper).lambda().from(ResourceCurrency.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<ResourceCurrency> queryPage(Pagination<ResourceCurrency> page, IWrapper<ResourceCurrency> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceCurrency().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<ResourceCurrency> queryListByWrapper(IWrapper<ResourceCurrency> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceCurrency().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<ResourceCurrency> queryListByEntity(ResourceCurrency entity) {
        //entity.set(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<ResourceCurrency> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public ResourceCurrency queryOneByWrapper(IWrapper<ResourceCurrency> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceCurrency().queryOneByWrapper(queryWrapper);
    }

    @Override
    @Function
    public ResourceCurrency queryByCode(String code) {
        return new ResourceCurrency().queryByCode(code);
    }

    @Override
    @Function
    public List<ResourceCurrency> queryByCodes(List<String> codes) {
        return FetchUtil.fetchListByCodes(ResourceCurrency.MODEL_MODEL, codes);
    }

    @Override
    @Function
    public ResourceCurrency queryById(Long id) {
        return new ResourceCurrency().queryById(id);
    }

    @Override
    @Function
    public List<ResourceCurrency> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(ResourceCurrency.MODEL_MODEL, ids);
    }

    @Override
    @Function
    public ResourceCurrency queryByName(String name) {
        return new ResourceCurrency().setName(name).queryOne();
    }
}
