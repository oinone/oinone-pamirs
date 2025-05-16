package pro.shushi.pamirs.boot.common.api.init;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Map;

public interface LifecycleCompletedAllInit extends CommonApi {

    void process(AppLifecycleCommand command, Map<String/*module*/, ModuleDefinition> runModuleMap);

}
