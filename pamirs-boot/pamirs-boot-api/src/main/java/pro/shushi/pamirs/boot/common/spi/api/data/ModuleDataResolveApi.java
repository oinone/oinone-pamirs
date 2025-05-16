package pro.shushi.pamirs.boot.common.spi.api.data;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.domain.LifecycleModuleTriple;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;
import java.util.Set;

/**
 * 模块业务数据初始化处理API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ModuleDataResolveApi {

    LifecycleModuleTriple resolve(AppLifecycleCommand command, Set<String> runModuleSet, Set<String> sortedModule,
                                  List<ModuleDefinition> installModules,
                                  List<ModuleDefinition> upgradeModules,
                                  List<ModuleDefinition> reloadModules);

}
