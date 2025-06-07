package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourcePartnerAddress;

import java.util.List;

@Fun(ResourcePartnerAddressService.FUN_NAMESPACE)
public interface ResourcePartnerAddressService {

    String FUN_NAMESPACE = "libra.resource.ResourcePartnerAddressService";

    /**
     * 分页查询接口
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    @Function
    Pagination<ResourcePartnerAddress> queryPage(Pagination<ResourcePartnerAddress> page, IWrapper<ResourcePartnerAddress> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    List<ResourcePartnerAddress> queryListByWrapper(IWrapper<ResourcePartnerAddress> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param entity
     * @return
     */
    @Function
    List<ResourcePartnerAddress> queryListByEntity(ResourcePartnerAddress entity);

    /**
     * 根据查询条件count
     *
     * @param queryWrapper
     * @return
     */
    @Function
    Long count(IWrapper<ResourcePartnerAddress> queryWrapper);

    /**
     * one查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    ResourcePartnerAddress queryOneByWrapper(IWrapper<ResourcePartnerAddress> queryWrapper);

    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    @Function
    ResourcePartnerAddress queryById(Long id);

    /**
     * 根据ids查询接口
     *
     * @param ids
     * @return
     */
    @Function
    List<ResourcePartnerAddress> queryByIds(List<Long> ids);

}
