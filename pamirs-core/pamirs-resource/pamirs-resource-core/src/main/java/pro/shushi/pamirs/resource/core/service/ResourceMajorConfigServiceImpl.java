package pro.shushi.pamirs.resource.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.model.ResourceMajorConfig;
import pro.shushi.pamirs.resource.api.service.ResourceMajorConfigService;

/**
 * {@link ResourceMajorConfigService}实现
 *
 * @author Adamancy Zhang at 19:31 on 2022-04-01
 */
@Service
@Fun(ResourceMajorConfigService.FUN_NAMESPACE)
public class ResourceMajorConfigServiceImpl implements ResourceMajorConfigService {

    @Function
    @Override
    public ResourceMajorConfig majorConfig() {
        ResourceMajorConfig resourceMajorConfig = new ResourceMajorConfig().singletonModel();
        if (resourceMajorConfig == null) {
            resourceMajorConfig = new ResourceMajorConfig();
        }
        if (StringUtils.isBlank(resourceMajorConfig.getDefaultAppLogo())) {
            resourceMajorConfig.setDefaultAppLogo(DefaultResourceConstants.DEFAULT_BRAND_LOGO);
        }
        return resourceMajorConfig;
    }

    @Function
    @Override
    public ResourceMajorConfig modifyAppSideLogo(ResourceMajorConfig config) {
        ResourceMajorConfig resourceMajorConfig = new ResourceMajorConfig().singletonModel();
        resourceMajorConfig.setAppSideLogo(config.getAppSideLogo());
        resourceMajorConfig.updateById();
        resourceMajorConfig.cleanCache();
        return resourceMajorConfig;
    }

    @Function
    @Override
    public ResourceMajorConfig modifyLoginPageLogo(ResourceMajorConfig config) {
        ResourceMajorConfig resourceMajorConfig = new ResourceMajorConfig().singletonModel();
        resourceMajorConfig.setLoginPageLogo(config.getLoginPageLogo());
        resourceMajorConfig.updateById();
        resourceMajorConfig.cleanCache();
        return resourceMajorConfig;
    }
}
