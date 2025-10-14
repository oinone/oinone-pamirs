package pro.shushi.pamirs.boot.common.spi.service.data;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.ReloadDataInit;
import pro.shushi.pamirs.boot.common.spi.api.data.ModuleDataReloadApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;

/**
 * 模块重启初始化数据API
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
public class DefaultModuleDataReload implements ModuleDataReloadApi {

    @Override
    public void reload(AppLifecycleCommand command, List<ModuleDefinition> modules, List<ReloadDataInit> reloadDataInits) {
        if (!command.getOptions().isUpdateData()) {
            return;
        }
        if (CollectionUtils.isEmpty(modules)) {
            return;
        }
        for (ModuleDefinition reloadModule : modules) {
            for (ReloadDataInit reloadDataInit : reloadDataInits) {
                List<String> targetModules = reloadDataInit.modules();
                if (!CollectionUtils.isEmpty(targetModules) && !targetModules.contains(reloadModule.getModule())) {
                    continue;
                }
                long start = System.currentTimeMillis();
                reloadDataInit.reload(command, reloadModule.getLatestVersion());
                log.info("{} reload data init cost time: {}ms", reloadDataInit.getClass().getName(), System.currentTimeMillis() - start);
            }
        }
    }

}
