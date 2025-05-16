package pro.shushi.pamirs.boot.orm.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.domain.LifecycleModuleGroup;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModuleLifecycleApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 启动模块生命周期接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(88)
@Component
@SPI.Service
public class OrmBootModuleLifecycle implements BootModuleLifecycleApi {

    @Override
    public LifecycleModuleGroup states(AppLifecycleCommand command, Map<String, ModuleDefinition> setupModuleMap, Set<String> runModules) {
        List<ModuleDefinition> installModules = new ArrayList<>(setupModuleMap.values());
        List<ModuleDefinition> upgradeModules = new ArrayList<>(0);
        List<ModuleDefinition> reloadModules = new ArrayList<>(0);

        return (LifecycleModuleGroup) new LifecycleModuleGroup()
                .setInstallModules(installModules)
                .setUpgradeModules(upgradeModules)
                .setReloadModules(reloadModules);
    }

}
