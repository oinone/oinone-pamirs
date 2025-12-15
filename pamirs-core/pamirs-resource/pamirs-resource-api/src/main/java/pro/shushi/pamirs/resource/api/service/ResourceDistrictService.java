package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourceDistrict;

import java.util.List;

@Fun(ResourceDistrictService.FUN_NAMESPACE)
public interface ResourceDistrictService {

    String FUN_NAMESPACE = "resource.ResourceDistrictService";

    /**
     * 分页查询接口
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    @Function
    Pagination<ResourceDistrict> queryPage(Pagination<ResourceDistrict> page, IWrapper<ResourceDistrict> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    List<ResourceDistrict> queryListByWrapper(IWrapper<ResourceDistrict> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param entity
     * @return
     */
    @Function
    List<ResourceDistrict> queryListByEntity(ResourceDistrict entity);

    /**
     * 根据查询条件count
     *
     * @param queryWrapper
     * @return
     */
    @Function
    Long count(IWrapper<ResourceDistrict> queryWrapper);

    /**
     * one查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    ResourceDistrict queryOneByWrapper(IWrapper<ResourceDistrict> queryWrapper);

    /**
     * 根据code查询接口
     *
     * @param code
     * @return
     */
    @Function
    ResourceDistrict queryByCode(String code);

    /**
     * 根据codes查询接口
     *
     * @param codes
     * @return
     */
    @Function
    List<ResourceDistrict> queryByCodes(List<String> codes);

    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    @Function
    ResourceDistrict queryById(Long id);

    /**
     * 根据ids查询接口
     *
     * @param ids
     * @return
     */
    @Function
    List<ResourceDistrict> queryByIds(List<Long> ids);

    @Function
    ResourceDistrict create(ResourceDistrict data);

}
