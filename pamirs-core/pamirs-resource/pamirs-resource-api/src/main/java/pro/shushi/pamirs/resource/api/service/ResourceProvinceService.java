package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourceProvince;

import java.util.List;

@Fun(ResourceProvinceService.FUN_NAMESPACE)
public interface ResourceProvinceService {

    String FUN_NAMESPACE = "libra.resource.ResourceProvinceService";

    /**
     * 分页查询接口
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    @Function
    Pagination<ResourceProvince> queryPage(Pagination<ResourceProvince> page, IWrapper<ResourceProvince> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    List<ResourceProvince> queryListByWrapper(IWrapper<ResourceProvince> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param entity
     * @return
     */
    @Function
    List<ResourceProvince> queryListByEntity(ResourceProvince entity);

    /**
     * 根据查询条件count
     *
     * @param queryWrapper
     * @return
     */
    @Function
    Long count(IWrapper<ResourceProvince> queryWrapper);

    /**
     * one查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    ResourceProvince queryOneByWrapper(IWrapper<ResourceProvince> queryWrapper);

    /**
     * 根据code查询接口
     *
     * @param code
     * @return
     */
    @Function
    ResourceProvince queryByCode(String code);

    /**
     * 根据codes查询接口
     *
     * @param codes
     * @return
     */
    @Function
    List<ResourceProvince> queryByCodes(List<String> codes);

    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    @Function
    ResourceProvince queryById(Long id);

    /**
     * 根据ids查询接口
     *
     * @param ids
     * @return
     */
    @Function
    List<ResourceProvince> queryByIds(List<Long> ids);

    @Function
    ResourceProvince queryByName(String name);
}
