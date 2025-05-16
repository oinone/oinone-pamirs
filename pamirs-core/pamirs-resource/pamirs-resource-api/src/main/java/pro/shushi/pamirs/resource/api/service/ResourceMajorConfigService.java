package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.resource.api.model.ResourceMajorConfig;

/**
 * {@link ResourceMajorConfig}服务
 *
 * @author Adamancy Zhang at 19:30 on 2022-04-01
 */
@Fun(ResourceMajorConfigService.FUN_NAMESPACE)
public interface ResourceMajorConfigService {

    String FUN_NAMESPACE = "resource.ResourceMajorConfigService";

    @Function
    ResourceMajorConfig majorConfig();

    @Function
    ResourceMajorConfig modifyAppSideLogo(ResourceMajorConfig config);

    @Function
    ResourceMajorConfig modifyLoginPageLogo(ResourceMajorConfig config);
}
