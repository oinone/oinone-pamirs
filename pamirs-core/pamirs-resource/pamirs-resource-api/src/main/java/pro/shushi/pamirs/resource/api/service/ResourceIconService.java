package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourceIcon;

@Fun(ResourceIconService.FUN_NAMESPACE)
public interface ResourceIconService {

    String FUN_NAMESPACE = "resource.ResourceIconService";

    @Function
    Pagination<ResourceIcon> queryPage(Pagination<ResourceIcon> page, IWrapper<ResourceIcon> queryWrapper);

    @Function
    ResourceIcon update(ResourceIcon data);

    @Function
    ResourceIcon deleteOne(ResourceIcon data);

    @Function
    ResourceIcon active(ResourceIcon data);

    @Function
    ResourceIcon disabled(ResourceIcon data);

    @Function
    ResourceIcon queryIcon(String fullFontClass);
}
