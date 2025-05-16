package pro.shushi.pamirs.boot.common.spi.service.boot;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.LifecycleCompletedInit;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModuleLifecycleCompletedApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
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
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultBootModuleLifecycleCompleted implements BootModuleLifecycleCompletedApi {

    @Override
    public void run(AppLifecycleCommand command,
                    List<ModuleDefinition> installModules,
                    List<ModuleDefinition> upgradeModules,
                    List<ModuleDefinition> reloadModules) {
        for (LifecycleCompletedInit init : BeanDefinitionUtils.getBeansOfTypeByOrdered(LifecycleCompletedInit.class)) {
            long start = System.currentTimeMillis();
            init.process(command, installModules, upgradeModules, reloadModules);
            log.info("{} lifecycle completed init cost time: {}ms", init.getClass().getName(), System.currentTimeMillis() - start);
        }
    }
}
