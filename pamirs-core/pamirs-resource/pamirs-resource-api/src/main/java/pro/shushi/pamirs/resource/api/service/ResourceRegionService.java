package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourceAddress;
import pro.shushi.pamirs.resource.api.model.ResourceRegion;

import java.util.List;

@Fun(ResourceRegionService.FUN_NAMESPACE)
public interface ResourceRegionService {

    String FUN_NAMESPACE = "pamirs.resource.ResourceRegionService";

    @Function
    ResourceRegion queryById(Long id);

    @Function
    ResourceRegion queryByCode(String code);

    /**
     * @param name  名称
     * @param level region对应的等级
     * @param pCode 上一级的code 查询level=1的时候可为空
     * @return
     */
    @Function
    ResourceRegion queryByName(String name, Integer level, String pCode);

    @Function
    String queryRegionCodeByName(ResourceAddress resourceAddress);

    @Function
    ResourceRegion updateRegionFile(ResourceRegion region);

    @Function
    ResourceRegion create(ResourceRegion region);

    @Function
    ResourceRegion queryOne(ResourceRegion region);

    @Function
    ResourceRegion queryOneByWrapper(IWrapper<ResourceRegion> queryWrapper);

    /**
     * 集合查询接口
     *
     * @param queryWrapper
     * @return
     */
    @Function
    List<ResourceRegion> queryListByWrapper(IWrapper<ResourceRegion> queryWrapper);

    @Function
    List<ResourceRegion> queryListByPage(Pagination<ResourceRegion> page, IWrapper<ResourceRegion> queryWrapper);

}
