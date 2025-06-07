package pro.shushi.pamirs.boot.common.spi.api.boot;

import pro.shushi.pamirs.framework.configure.annotation.core.ModuleResolver;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Map;
import java.util.Set;

/**
 * 启动获取模块列表接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface BootModulesApi {

    default Set<String> modules() {
        return null;
    }

    default Set<String> excludeModules() {
        return null;
    }

    default Set<String> distributionModules() {
        return null;
    }

    default Map<String/*module*/, ModuleDefinition> jarModules() {
        return BeanDefinitionUtils.getBean(ModuleResolver.class).resolve();
    }

}
