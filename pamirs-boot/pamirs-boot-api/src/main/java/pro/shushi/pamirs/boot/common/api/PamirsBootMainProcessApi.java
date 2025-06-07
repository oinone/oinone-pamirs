package pro.shushi.pamirs.boot.common.api;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Map;
import java.util.Set;

/**
 * 生命周期管理接口
 * 2021/2/25 1:45 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface PamirsBootMainProcessApi {

    Boolean installOrLoad(final ComputeContext context, final Set<String> modules, final Set<String> excludeModules,
                          AppLifecycleCommand command);

    Map<String, ModuleDefinition> fetchSetupModuleMap();

    Set<String> fetchBootModules(Map<String, ModuleDefinition> setupModuleMap, Set<String> modules, Set<String> excludeModules);

}
