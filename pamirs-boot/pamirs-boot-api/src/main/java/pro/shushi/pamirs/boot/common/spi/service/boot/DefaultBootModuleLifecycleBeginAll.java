package pro.shushi.pamirs.boot.common.spi.service.boot;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.LifecycleBeginAllInit;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModuleLifecycleBeginAllApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Map;

/**
 * 启动模块生命周期全部开始前处理接口
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
public class DefaultBootModuleLifecycleBeginAll implements BootModuleLifecycleBeginAllApi {

    @Override
    public void run(AppLifecycleCommand command, Map<String/*module*/, ModuleDefinition> setupModuleMap) {
        for (LifecycleBeginAllInit init : BeanDefinitionUtils.getBeansOfTypeByOrdered(LifecycleBeginAllInit.class)) {
            long start = System.currentTimeMillis();
            init.process(command, setupModuleMap);
            log.info("{} lifecycle begin all init cost time: {}ms", init.getClass().getName(), System.currentTimeMillis() - start);
        }
    }
}
