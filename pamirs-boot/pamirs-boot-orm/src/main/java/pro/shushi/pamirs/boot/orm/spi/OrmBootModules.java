package pro.shushi.pamirs.boot.orm.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModulesApi;
import pro.shushi.pamirs.boot.orm.configure.BootConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 启动获取模块列表接口
 * <p>
 * 2020/8/27 5:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Order(88)
@Component
@SPI.Service
public class OrmBootModules implements BootModulesApi {

    @Resource
    private BootConfiguration bootConfiguration;

    @Override
    public Set<String> modules() {
        return bootConfiguration.getModules();
    }

    @Override
    public Set<String> excludeModules() {
        return bootConfiguration.getExcludeModules();
    }

    @Override
    public Set<String> distributionModules() {
        return bootConfiguration.getDistributionModules();
    }

}
