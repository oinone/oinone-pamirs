package pro.shushi.pamirs.channel.api;

import pro.shushi.pamirs.channel.model.ChannelModel;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

@Fun(ChannelModelService.FUN_NAMESPACE)
public interface ChannelModelService {

    String FUN_NAMESPACE = "channel.ChannelModelService";

    /**
     * 分页查询接口
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    Pagination<ChannelModel> queryPage(Pagination<ChannelModel> page, IWrapper<ChannelModel> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param queryWrapper
     * @return
     */
    List<ChannelModel> queryListByWrapper(IWrapper<ChannelModel> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param entity
     * @return
     */
    List<ChannelModel> queryListByEntity(ChannelModel entity);

    /**
     * 根据查询条件count
     *
     * @param queryWrapper
     * @return
     */
    Long count(IWrapper<ChannelModel> queryWrapper);

    /**
     * one查询接口
     *
     * @param queryWrapper
     * @return
     */
    ChannelModel queryOneByWrapper(IWrapper<ChannelModel> queryWrapper);

    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    ChannelModel queryById(Long id);

    /**
     * 根据ids查询接口
     *
     * @param ids
     * @return
     */
    List<ChannelModel> queryByIds(List<Long> ids);

}
