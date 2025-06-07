package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.OutResourceRelation;

import java.util.List;

@Fun(OutResourceRelationService.FUN_NAMESPACE)
public interface OutResourceRelationService {

    String FUN_NAMESPACE = "core.resource.OutResourceRelationService";

    /**
     * 分页查询接口
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    @Function
    Pagination<OutResourceRelation> queryPage(Pagination<OutResourceRelation> page, IWrapper<OutResourceRelation> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    List<OutResourceRelation> queryListByWrapper(IWrapper<OutResourceRelation> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param entity
     * @return
     */
    @Function
    List<OutResourceRelation> queryListByEntity(OutResourceRelation entity);

    /**
     * 根据查询条件count
     *
     * @param queryWrapper
     * @return
     */
    @Function
    Long count(IWrapper<OutResourceRelation> queryWrapper);

    /**
     * one查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    OutResourceRelation queryOneByWrapper(IWrapper<OutResourceRelation> queryWrapper);

    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    @Function
    OutResourceRelation queryById(Long id);

    /**
     * 根据ids查询接口
     *
     * @param ids
     * @return
     */
    @Function
    List<OutResourceRelation> queryByIds(List<Long> ids);

}
