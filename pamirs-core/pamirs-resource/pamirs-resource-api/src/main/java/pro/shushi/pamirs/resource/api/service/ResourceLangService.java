package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.resource.api.model.ResourceLang;

@Fun(ResourceLangService.FUN_NAMESPACE)
public interface ResourceLangService {

    String FUN_NAMESPACE = "pamirs.resource.ResourceLangService";

    /**
     * 根据id查询接口
     *
     * @param id
     * @return
     */
    @Function
    ResourceLang queryById(Long id);

}
