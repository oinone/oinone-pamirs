package pro.shushi.pamirs.boot.common.spi.api.boot;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 启动模块生命周期环切接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface BootModuleLifecycleAroundApi {

    default void run(AppLifecycleCommand command, Consumer<AppLifecycleCommand> runner) {
        runner.accept(command);
    }

    default void run(AppLifecycleCommand command, ComputeContext context, Map<String, ModuleDefinition> setupModuleMap, Set<String> bootModuleSet,
                     BootModuleLifecycleAroundConsumer runner) {
        runner.accept(command, context, setupModuleMap, bootModuleSet);
    }

    interface BootModuleLifecycleAroundConsumer {

        void accept(AppLifecycleCommand command, ComputeContext context, Map<String, ModuleDefinition> setupModuleMap, Set<String> bootModuleSet);

    }
}
