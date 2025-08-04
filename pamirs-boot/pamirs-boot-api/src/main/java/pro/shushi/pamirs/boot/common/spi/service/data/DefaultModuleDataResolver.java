package pro.shushi.pamirs.boot.common.spi.service.data;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.domain.LifecycleModuleTriple;
import pro.shushi.pamirs.boot.common.spi.api.data.ModuleDataResolveApi;
import pro.shushi.pamirs.framework.configure.annotation.core.ModuleResolver;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 模块初始化数据预处理API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
public class DefaultModuleDataResolver implements ModuleDataResolveApi {

    @Resource
    private ModuleResolver moduleResolver;

    @Override
    public LifecycleModuleTriple resolve(AppLifecycleCommand command, Set<String> runModuleSet, Set<String> sortedModule,
                                         List<ModuleDefinition> installModules,
                                         List<ModuleDefinition> upgradeModules,
                                         List<ModuleDefinition> reloadModules) {
        List<ModuleDefinition> runInstallModules = moduleResolver.resolve(runModuleSet, sortedModule, installModules);
        List<ModuleDefinition> runUpgradeModules = moduleResolver.resolve(runModuleSet, sortedModule, upgradeModules);
        List<ModuleDefinition> runReloadModules = moduleResolver.resolve(runModuleSet, sortedModule, reloadModules);
        return new LifecycleModuleTriple()
                .setInstallModules(runInstallModules)
                .setUpgradeModules(runUpgradeModules)
                .setReloadModules(runReloadModules);
    }
}
