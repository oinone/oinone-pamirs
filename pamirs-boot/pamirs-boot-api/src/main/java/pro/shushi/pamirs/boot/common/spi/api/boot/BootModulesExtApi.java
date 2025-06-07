package pro.shushi.pamirs.boot.common.spi.api.boot;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Map;
import java.util.Set;

/**
 * BootModulesExtApi
 *
 * @author yakir on 2025/06/03 16:19.
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface BootModulesExtApi {

    default void dataModules(AppLifecycleCommand command, Map<String, ModuleDefinition> setupModuleMap, Set<String> bootModuleSet) {
    }

}
