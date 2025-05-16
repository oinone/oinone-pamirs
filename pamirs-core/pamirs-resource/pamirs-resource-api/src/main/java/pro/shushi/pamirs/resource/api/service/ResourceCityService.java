package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourceCity;

import java.util.List;

@Fun(ResourceCityService.FUN_NAMESPACE)
public interface ResourceCityService {

    String FUN_NAMESPACE = "libra.resource.ResourceCityService";

    /**
     * 分页查询接口
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    @Function
    Pagination<ResourceCity> queryPage(Pagination<ResourceCity> page, IWrapper<ResourceCity> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    List<ResourceCity> queryListByWrapper(IWrapper<ResourceCity> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param entity
     * @return
     */
    @Function
    List<ResourceCity> queryListByEntity(ResourceCity entity);

    /**
     * 根据查询条件count
     *
     * @param queryWrapper
     * @return
     */
    @Function
    Long count(IWrapper<ResourceCity> queryWrapper);

    /**
     * one查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    ResourceCity queryOneByWrapper(IWrapper<ResourceCity> queryWrapper);

    /**
     * 根据code查询接口
     *
     * @param code
     * @return
     */
    @Function
    ResourceCity queryByCode(String code);

    /**
     * 根据codes查询接口
     *
     * @param codes
     * @return
     */
    @Function
    List<ResourceCity> queryByCodes(List<String> codes);

    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    @Function
    ResourceCity queryById(Long id);

    /**
     * 根据ids查询接口
     *
     * @param ids
     * @return
     */
    @Function
    List<ResourceCity> queryByIds(List<Long> ids);

    @Function
    ResourceCity queryByName(String name);

}
