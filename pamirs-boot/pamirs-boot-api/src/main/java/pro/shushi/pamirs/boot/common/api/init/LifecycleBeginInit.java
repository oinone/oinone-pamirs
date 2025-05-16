package pro.shushi.pamirs.boot.common.api.init;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;
import java.util.Set;

public interface LifecycleBeginInit extends CommonApi {

    void process(AppLifecycleCommand command, Set<String> runModules,
                 List<ModuleDefinition> installModules,
                 List<ModuleDefinition> upgradeModules,
                 List<ModuleDefinition> reloadModules);

}
