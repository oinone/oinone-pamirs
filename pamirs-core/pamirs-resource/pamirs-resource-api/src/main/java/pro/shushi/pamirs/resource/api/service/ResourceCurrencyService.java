package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourceCurrency;

import java.util.List;

@Fun(ResourceCurrencyService.FUN_NAMESPACE)
public interface ResourceCurrencyService {

    String FUN_NAMESPACE = "libra.resource.ResourceCurrencyService";

    /**
     * 分页查询接口
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    @Function
    Pagination<ResourceCurrency> queryPage(Pagination<ResourceCurrency> page, IWrapper<ResourceCurrency> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    List<ResourceCurrency> queryListByWrapper(IWrapper<ResourceCurrency> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param entity
     * @return
     */
    @Function
    List<ResourceCurrency> queryListByEntity(ResourceCurrency entity);

    /**
     * 根据查询条件count
     *
     * @param queryWrapper
     * @return
     */
    @Function
    Long count(IWrapper<ResourceCurrency> queryWrapper);

    /**
     * one查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    ResourceCurrency queryOneByWrapper(IWrapper<ResourceCurrency> queryWrapper);

    /**
     * 根据code查询接口
     *
     * @param code
     * @return
     */
    @Function
    ResourceCurrency queryByCode(String code);

    /**
     * 根据codes查询接口
     *
     * @param codes
     * @return
     */
    @Function
    List<ResourceCurrency> queryByCodes(List<String> codes);

    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    @Function
    ResourceCurrency queryById(Long id);

    /**
     * 根据ids查询接口
     *
     * @param ids
     * @return
     */
    @Function
    List<ResourceCurrency> queryByIds(List<Long> ids);

    @Function
    ResourceCurrency queryByName(String name);
}
