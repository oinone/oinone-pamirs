package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.resource.api.model.ResourceCompanyNature;

@Fun(ResourceCompanyNatureService.FUN_NAMESPACE)
public interface ResourceCompanyNatureService {
    String FUN_NAMESPACE = "pamirs.resource.ResourceCompanyNatureService";

    @Function
    ResourceCompanyNature update(ResourceCompanyNature data);

    @Function
    ResourceCompanyNature create(ResourceCompanyNature data);
}
