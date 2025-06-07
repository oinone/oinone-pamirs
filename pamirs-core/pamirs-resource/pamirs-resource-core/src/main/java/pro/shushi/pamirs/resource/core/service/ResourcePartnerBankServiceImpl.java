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
import pro.shushi.pamirs.resource.api.model.ResourcePartnerBank;
import pro.shushi.pamirs.resource.api.service.ResourcePartnerBankService;

import java.util.List;

@Fun(ResourcePartnerBankService.FUN_NAMESPACE)
@Component
public class ResourcePartnerBankServiceImpl implements ResourcePartnerBankService {

    private IWrapper<ResourcePartnerBank> initQueryWrapper(IWrapper<ResourcePartnerBank> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ResourcePartnerBank>) queryWrapper).from(ResourcePartnerBank.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ResourcePartnerBank>) queryWrapper).lambda().from(ResourcePartnerBank.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<ResourcePartnerBank> queryPage(Pagination<ResourcePartnerBank> page, IWrapper<ResourcePartnerBank> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourcePartnerBank().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<ResourcePartnerBank> queryListByWrapper(IWrapper<ResourcePartnerBank> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourcePartnerBank().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<ResourcePartnerBank> queryListByEntity(ResourcePartnerBank entity) {
        //entity.set${ModelPartnerId}(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<ResourcePartnerBank> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public ResourcePartnerBank queryOneByWrapper(IWrapper<ResourcePartnerBank> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourcePartnerBank().queryOneByWrapper(queryWrapper);
    }


    @Override
    @Function
    public ResourcePartnerBank queryById(Long id) {
        return new ResourcePartnerBank().queryById(id);
    }

    @Override
    @Function
    public List<ResourcePartnerBank> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(ResourcePartnerBank.MODEL_MODEL, ids);
    }

    @Override
    @Function
    public ResourcePartnerBank create(ResourcePartnerBank resourcePartnerBank) {
        return resourcePartnerBank.create();
    }

    @Override
    @Function
    public ResourcePartnerBank updateById(ResourcePartnerBank resourcePartnerBank) {
        resourcePartnerBank.updateById();
        return resourcePartnerBank;
    }
}
