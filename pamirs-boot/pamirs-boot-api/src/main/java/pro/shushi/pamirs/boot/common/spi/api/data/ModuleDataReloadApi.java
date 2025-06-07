package pro.shushi.pamirs.boot.common.spi.api.data;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.ReloadDataInit;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;

/**
 * 模块重启数据更新API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ModuleDataReloadApi {

    void reload(AppLifecycleCommand command, List<ModuleDefinition> modules, List<ReloadDataInit> reloadDataInits);

}
