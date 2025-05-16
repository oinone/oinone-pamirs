package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourceRegionMapping;

import java.util.List;

@Fun(ResourceRegionMappingService.FUN_NAMESPACE)
public interface ResourceRegionMappingService {

    String FUN_NAMESPACE = "libra.resource.ResourceRegionMappingQueryService";

    /**
     * 分页查询接口
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    @Function
    Pagination<ResourceRegionMapping> queryPage(Pagination<ResourceRegionMapping> page, IWrapper<ResourceRegionMapping> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    List<ResourceRegionMapping> queryListByWrapper(IWrapper<ResourceRegionMapping> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param entity
     * @return
     */
    @Function
    List<ResourceRegionMapping> queryListByEntity(ResourceRegionMapping entity);

    /**
     * 根据查询条件count
     *
     * @param queryWrapper
     * @return
     */
    Long count(IWrapper<ResourceRegionMapping> queryWrapper);

    /**
     * one查询接口
     *
     * @param queryWrapper
     * @return
     */
    ResourceRegionMapping queryOneByWrapper(IWrapper<ResourceRegionMapping> queryWrapper);


    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    ResourceRegionMapping queryById(Long id);

    /**
     * 根据ids查询接口
     *
     * @param ids
     * @return
     */
    List<ResourceRegionMapping> queryByIds(List<Long> ids);


    List<ResourceRegionMapping> createBatch(List<ResourceRegionMapping> dataList);

}
