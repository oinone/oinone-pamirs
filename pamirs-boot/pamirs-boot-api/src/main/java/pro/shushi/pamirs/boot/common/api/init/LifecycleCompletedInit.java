package pro.shushi.pamirs.boot.common.api.init;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;

public interface LifecycleCompletedInit extends CommonApi {

    void process(AppLifecycleCommand command,
                 List<ModuleDefinition> installModules,
                 List<ModuleDefinition> upgradeModules,
                 List<ModuleDefinition> reloadModules);

}
