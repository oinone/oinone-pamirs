package pro.shushi.pamirs.framework.faas.load;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.faas.boot.ModulesApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.stl.ConcurrentHashSet;

import java.util.Set;

/**
 * 启动模块api实现
 * 2021/1/26 9:30 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI.Service
@Component
public class DefaultModuleApi implements ModulesApi {

    private static final Set<String> bootModules = new ConcurrentHashSet<>();

    @Override
    public Set<String> modules() {
        return bootModules;
    }

    @Override
    public void setModules(Set<String> modules) {
        bootModules.addAll(modules);
    }

}
