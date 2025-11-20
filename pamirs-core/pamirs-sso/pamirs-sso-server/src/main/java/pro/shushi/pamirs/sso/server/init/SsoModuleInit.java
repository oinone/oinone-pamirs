package pro.shushi.pamirs.sso.server.init;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.sso.api.SsoModule;
import pro.shushi.pamirs.sso.api.model.SsoClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@Order(90)
@Component
public class SsoModuleInit implements MetaDataEditor, InstallDataInit, UpgradeDataInit {

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        return false;
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        return false;
    }

    @Override
    public List<String> modules() {
        return Collections.singletonList(SsoModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, SsoModule.MODULE_MODULE, SsoModule.MODULE_NAME);
        if (util == null) {
            return;
        }
        modifyViewAction(util);
    }

    private void modifyViewAction(InitializationUtil util) {
        util.modifyViewAction(SsoClient.MODEL_MODEL, ViewActionConstants.Import.name,createVA -> createVA.setInvisible("true"));
        util.modifyViewAction(SsoClient.MODEL_MODEL, ViewActionConstants.Export.name, updateVA -> updateVA.setInvisible("true"));
    }
}
