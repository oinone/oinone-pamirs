package pro.shushi.pamirs.boot.common.spi.api.boot;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;

/**
 * 启动模块生命周期完成接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface BootModuleLifecycleCompletedApi {

    void run(AppLifecycleCommand command,
             List<ModuleDefinition> installModules,
             List<ModuleDefinition> upgradeModules,
             List<ModuleDefinition> reloadModules);

}
