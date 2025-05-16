package pro.shushi.pamirs.boot.common.spi.service.boot;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.domain.LifecycleModuleGroup;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModuleLifecycleApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Map;
import java.util.Set;

/**
 * 启动模块生命周期状态接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
@SPI.Service
public class DefaultBootModuleLifecycle implements BootModuleLifecycleApi {

    @Override
    public LifecycleModuleGroup states(AppLifecycleCommand command, Map<String, ModuleDefinition> setupModuleMap, Set<String> runModules) {
        // 计算需要安装或者升级的模块
        return new LifecycleModuleGroup();
    }

}
