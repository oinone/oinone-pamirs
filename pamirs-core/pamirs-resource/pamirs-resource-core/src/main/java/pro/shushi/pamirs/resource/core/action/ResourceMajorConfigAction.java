package pro.shushi.pamirs.resource.core.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceMajorConfig;
import pro.shushi.pamirs.resource.api.service.ResourceMajorConfigService;

@Component
@Model.model(ResourceMajorConfig.MODEL_MODEL)
public class ResourceMajorConfigAction {

    @Autowired
    private ResourceMajorConfigService resourceMajorConfigService;

    @Action(contextType = ActionContextTypeEnum.CONTEXT_FREE, displayName = "系统基本配置")
    public ResourceMajorConfig majorConfig() {
        return resourceMajorConfigService.majorConfig();
    }


    @Action(contextType = ActionContextTypeEnum.CONTEXT_FREE, displayName = "修改AppSideLogo")
    public ResourceMajorConfig modifyAppSideLogo(ResourceMajorConfig config) {
        return resourceMajorConfigService.modifyAppSideLogo(config);
    }

    @Action(contextType = ActionContextTypeEnum.CONTEXT_FREE, displayName = "修改登录页背景图")
    public ResourceMajorConfig modifyLoginPageLogo(ResourceMajorConfig config) {
        return resourceMajorConfigService.modifyLoginPageLogo(config);
    }


}
