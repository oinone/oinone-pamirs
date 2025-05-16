package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.resource.api.model.ResourceIconGroup;

import java.util.List;

@Fun(ResourceIconGroupService.FUN_NAMESPACE)
public interface ResourceIconGroupService{

    String FUN_NAMESPACE = "resource.ResourceIconGroupService";

    @Function
    List<ResourceIconGroup> queryAllGroupData();

    @Function
    List<ResourceIconGroup> queryGroupData();

    @Function
    ResourceIconGroup create(ResourceIconGroup data);

    @Function
    ResourceIconGroup update(ResourceIconGroup data);

    @Function
    ResourceIconGroup deleteOne(ResourceIconGroup data);
}
