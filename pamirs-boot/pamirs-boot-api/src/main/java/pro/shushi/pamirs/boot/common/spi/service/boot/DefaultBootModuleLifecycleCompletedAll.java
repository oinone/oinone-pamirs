package pro.shushi.pamirs.boot.common.spi.service.boot;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.LifecycleCompletedAllInit;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModuleLifecycleCompletedAllApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Map;

/**
 * 启动模块生命周期全部完成后处理接口
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
public class DefaultBootModuleLifecycleCompletedAll implements BootModuleLifecycleCompletedAllApi {

    @Override
    public void run(AppLifecycleCommand command, Map<String/*module*/, ModuleDefinition> setupModuleMap) {
        for (LifecycleCompletedAllInit init : BeanDefinitionUtils.getBeansOfTypeByOrdered(LifecycleCompletedAllInit.class)) {
            long start = System.currentTimeMillis();
            init.process(command, setupModuleMap);
            log.info("{} lifecycle completed all init cost time: {}ms", init.getClass().getName(), System.currentTimeMillis() - start);
        }
    }
}
