package pro.shushi.pamirs.eip.core.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.eip.api.EipModule;
import pro.shushi.pamirs.eip.core.task.EipLogCountSyncTask;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author yeshenyue on 2025/4/10 19:10.
 */
@Component
public class EipLogCountInit implements InstallDataInit, UpgradeDataInit {

    @Resource
    private EipLogCountSyncTask eipLogCountSyncTask;

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        eipLogCountSyncTask.initTask();
        return true;
    }

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        eipLogCountSyncTask.initTask();
        return true;
    }

    @Override
    public List<String> modules() {
        return Collections.singletonList(EipModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 0;
    }
}
