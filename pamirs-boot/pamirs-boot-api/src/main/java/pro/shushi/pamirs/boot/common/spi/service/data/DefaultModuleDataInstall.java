package pro.shushi.pamirs.boot.common.spi.service.data;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.spi.api.data.ModuleDataInstallApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;

/**
 * 模块安装初始化数据API
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
public class DefaultModuleDataInstall implements ModuleDataInstallApi {

    @Override
    public void install(AppLifecycleCommand command, List<ModuleDefinition> modules, List<InstallDataInit> installDataInits) {
        if (!command.getOptions().isUpdateData()) {
            return;
        }
        if (CollectionUtils.isEmpty(modules)) {
            return;
        }
        for (ModuleDefinition installModule : modules) {
            for (InstallDataInit installDataInit : installDataInits) {
                List<String> targetModules = installDataInit.modules();
                if (!CollectionUtils.isEmpty(targetModules) && !targetModules.contains(installModule.getModule())) {
                    continue;
                }
                long start = System.currentTimeMillis();
                installDataInit.init(command, installModule.getLatestVersion());
                log.info("{} install data init cost time: {}ms", installDataInit.getClass().getName(), System.currentTimeMillis() - start);
            }
        }
    }

}
