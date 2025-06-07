package pro.shushi.pamirs.sso.oauth2.server.init;

import com.google.common.collect.Lists;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.sso.api.SsoModule;
import pro.shushi.pamirs.sso.api.model.SsoOauth2ClientDetails;

import java.util.Map;

@Order(100)
@Component
public class SsoMetadataEdit implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, SsoModule.MODULE_MODULE, SsoModule.MODULE_NAME);
        if (util == null) {
            return;
        }
        util.createUrlAction("redirectHomePage", "跳转首页", "${activeRecord.homepageUrl}", SsoOauth2ClientDetails.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), ActionContextTypeEnum.SINGLE, ActionTargetEnum.OPEN_WINDOW, "");
    }
}
