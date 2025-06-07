package pro.shushi.pamirs.business.core.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.business.api.BusinessModule;

import java.util.Collections;
import java.util.List;

/**
 * BusinessDataInit
 *
 * @author yakir on 2022/09/13 17:40.
 */
@Component
public class BusinessDataInit implements InstallDataInit, UpgradeDataInit {

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
        return Collections.singletonList(BusinessModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 0;
    }
}
