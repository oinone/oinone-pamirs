package pro.shushi.pamirs.boot.common.spi.service.data;

import org.apache.commons.collections4.MapUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.boot.common.spi.api.data.ModuleDataUpgradeApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 模块升级初始化数据API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultModuleDataUpgrade implements ModuleDataUpgradeApi {

    @Override
    public void upgrade(AppLifecycleCommand command, Map<String, ModuleDefinition> installedModuleMap,
                        List<ModuleDefinition> modules, List<UpgradeDataInit> upgradeDataInits) {
        if (!command.getOptions().isUpdateData()) {
            return;
        }
        if (MapUtils.isEmpty(installedModuleMap) || CollectionUtils.isEmpty(modules)) {
            return;
        }
        for (ModuleDefinition upgradeModule : modules) {
            for (UpgradeDataInit upgradeDataInit : upgradeDataInits) {
                List<String> targetModules = upgradeDataInit.modules();
                if (!CollectionUtils.isEmpty(targetModules) && !targetModules.contains(upgradeModule.getModule())) {
                    continue;
                }
                String currentInstallVersion = Optional.ofNullable(installedModuleMap.get(upgradeModule.getModule()))
                        .map(ModuleDefinition::getLatestVersion)
                        .orElse(upgradeModule.getLatestVersion());
                long start = System.currentTimeMillis();
                upgradeDataInit.upgrade(command, upgradeModule.getLatestVersion(), currentInstallVersion);
                log.info("{} upgrade data init cost time: {}ms", upgradeDataInit.getClass().getName(), System.currentTimeMillis() - start);
            }
        }
    }

}
