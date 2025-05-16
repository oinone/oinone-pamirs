package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.resource.api.model.ResourceCityLevel;

import java.util.List;

@Fun(ResourceCityLevelService.FUN_NAMESPACE)
public interface ResourceCityLevelService {
    String FUN_NAMESPACE = "pamirs.resource.ResourceCityLevelService";

    @Function
    List<ResourceCityLevel> deleteBatch(List<ResourceCityLevel> data);

    @Function
    ResourceCityLevel update(ResourceCityLevel data);

    @Function
    ResourceCityLevel queryOne(ResourceCityLevel query);
}
