package pro.shushi.pamirs.apps.core.init;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.AppsModule;
import pro.shushi.pamirs.apps.api.pmodel.AppsManagementModule;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.Map;

/**
 * @author shier
 * date  2021/5/24 2:39 下午
 */
@Component
public class AppsModuleViewInit implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, AppsModule.MODULE_MODULE, AppsModule.MODULE_NAME);
        if (util == null) {
            return;
        }
        util.createMask("apps_business_screen_page", "file:pamirs/views/apps/mask/apps_business_screen_page.xml");
        util.createViewAction("apps_business_screen_detail", "了解更多",
                AppsManagementModule.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.GALLERY),
                AppsManagementModule.MODEL_MODEL, ViewTypeEnum.DETAIL,
                ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER,
                "apps_module_detail", null, v -> {
                    v.setMask("apps_business_screen_page");
                });
    }
}
