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
import pro.shushi.pamirs.resource.api.model.OutResourceRelation;
import pro.shushi.pamirs.resource.api.service.OutResourceRelationService;

import java.util.List;

@Fun(OutResourceRelationService.FUN_NAMESPACE)
@Component
public class OutResourceRelationServiceImpl implements OutResourceRelationService {

    private IWrapper<OutResourceRelation> initQueryWrapper(IWrapper<OutResourceRelation> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<OutResourceRelation>) queryWrapper).from(OutResourceRelation.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<OutResourceRelation>) queryWrapper).lambda().from(OutResourceRelation.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<OutResourceRelation> queryPage(Pagination<OutResourceRelation> page, IWrapper<OutResourceRelation> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new OutResourceRelation().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<OutResourceRelation> queryListByWrapper(IWrapper<OutResourceRelation> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new OutResourceRelation().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<OutResourceRelation> queryListByEntity(OutResourceRelation entity) {
        //entity.set${ModelPartnerId}(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<OutResourceRelation> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public OutResourceRelation queryOneByWrapper(IWrapper<OutResourceRelation> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new OutResourceRelation().queryOneByWrapper(queryWrapper);
    }

    @Override
    @Function
    public OutResourceRelation queryById(Long id) {
        return new OutResourceRelation().queryById(id);
    }

    @Override
    @Function
    public List<OutResourceRelation> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(OutResourceRelation.MODEL_MODEL, ids);
    }
}
