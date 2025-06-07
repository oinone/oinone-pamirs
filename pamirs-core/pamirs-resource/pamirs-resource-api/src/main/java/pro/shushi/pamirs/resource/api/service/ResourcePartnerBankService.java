package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourcePartnerBank;

import java.util.List;

@Fun(ResourcePartnerBankService.FUN_NAMESPACE)
public interface ResourcePartnerBankService {

    String FUN_NAMESPACE = "core.resource.ResourcePartnerBankService";

    /**
     * 分页查询接口
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    @Function
    Pagination<ResourcePartnerBank> queryPage(Pagination<ResourcePartnerBank> page, IWrapper<ResourcePartnerBank> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    List<ResourcePartnerBank> queryListByWrapper(IWrapper<ResourcePartnerBank> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param entity
     * @return
     */
    @Function
    List<ResourcePartnerBank> queryListByEntity(ResourcePartnerBank entity);

    /**
     * 根据查询条件count
     *
     * @param queryWrapper
     * @return
     */
    @Function
    Long count(IWrapper<ResourcePartnerBank> queryWrapper);

    /**
     * one查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    ResourcePartnerBank queryOneByWrapper(IWrapper<ResourcePartnerBank> queryWrapper);


    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    @Function
    ResourcePartnerBank queryById(Long id);

    /**
     * 根据ids查询接口
     *
     * @param ids
     * @return
     */
    @Function
    List<ResourcePartnerBank> queryByIds(List<Long> ids);

    @Function
    ResourcePartnerBank create(ResourcePartnerBank resourcePartnerBank);

    @Function
    ResourcePartnerBank updateById(ResourcePartnerBank resourcePartnerBank);

}
