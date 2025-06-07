package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourceAddress;

import java.util.List;

@Fun(ResourceAddressService.FUN_NAMESPACE)
public interface ResourceAddressService {

    String FUN_NAMESPACE = "libra.resource.ResourceAddressService";

    /**
     * 分页查询接口
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    @Function
    Pagination<ResourceAddress> queryPage(Pagination<ResourceAddress> page, IWrapper<ResourceAddress> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    List<ResourceAddress> queryListByWrapper(IWrapper<ResourceAddress> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param entity
     * @return
     */
    @Function
    List<ResourceAddress> queryListByEntity(ResourceAddress entity);

    /**
     * 根据查询条件count
     *
     * @param queryWrapper
     * @return
     */
    @Function
    Long count(IWrapper<ResourceAddress> queryWrapper);

    /**
     * one查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    ResourceAddress queryOneByWrapper(IWrapper<ResourceAddress> queryWrapper);


    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    @Function
    ResourceAddress queryById(Long id);

    /**
     * 根据ids查询接口
     *
     * @param ids
     * @return
     */
    @Function
    List<ResourceAddress> queryByIds(List<Long> ids);

    @Function
    Integer updateById(ResourceAddress data);

    @Function
    ResourceAddress create(ResourceAddress data);
}
