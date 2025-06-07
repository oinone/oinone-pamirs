package pro.shushi.pamirs.boot.common.spi.api.meta;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;
import java.util.Set;

/**
 * 元数据升级加载API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface MetaDataUpgraderApi {

    /**
     * 差量减
     *
     * @param command              生命周期指令
     * @param metaList             元数据
     * @param includeModuleModules 包含模块
     * @param excludeModules       排除模块
     */
    default void diffDelete(AppLifecycleCommand command, List<Meta> metaList,
                            Set<String> includeModuleModules, List<ModuleDefinition> excludeModules) {

    }

}
