package pro.shushi.pamirs.channel.core.service;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.channel.api.ChannelModelService;
import pro.shushi.pamirs.channel.model.ChannelModel;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

@Fun(ChannelModelService.FUN_NAMESPACE)
@Component
public class ChannelModelServiceImpl implements ChannelModelService {

    private IWrapper<ChannelModel> initQueryWrapper(IWrapper<ChannelModel> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ChannelModel>) queryWrapper).from(ChannelModel.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ChannelModel>) queryWrapper).lambda().from(ChannelModel.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<ChannelModel> queryPage(Pagination<ChannelModel> page, IWrapper<ChannelModel> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ChannelModel().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<ChannelModel> queryListByWrapper(IWrapper<ChannelModel> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ChannelModel().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<ChannelModel> queryListByEntity(ChannelModel entity) {
        //entity.set${ModelPartnerId}(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<ChannelModel> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public ChannelModel queryOneByWrapper(IWrapper<ChannelModel> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ChannelModel().queryOneByWrapper(queryWrapper);
    }

    @Override
    @Function
    public ChannelModel queryById(Long id) {
        return new ChannelModel().queryById(id);
    }

    @Override
    @Function
    public List<ChannelModel> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(ChannelModel.MODEL_MODEL, ids);
    }
}
