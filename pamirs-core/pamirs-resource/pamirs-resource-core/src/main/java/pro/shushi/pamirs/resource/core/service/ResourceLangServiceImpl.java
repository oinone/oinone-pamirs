package pro.shushi.pamirs.resource.core.service;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.resource.api.service.ResourceLangService;

/**
 * @author Nation
 * @cdate 2021-04-02 10:39
 */
@Component
@Fun(ResourceLangService.FUN_NAMESPACE)
public class ResourceLangServiceImpl implements ResourceLangService {

    @Override
    @Function
    public ResourceLang queryById(Long id) {
        if (id == null) {
            return null;
        }
        return new ResourceLang().queryById(id);
    }

}
