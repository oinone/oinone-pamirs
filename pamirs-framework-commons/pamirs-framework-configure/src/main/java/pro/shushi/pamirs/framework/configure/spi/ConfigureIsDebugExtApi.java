package pro.shushi.pamirs.framework.configure.spi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.IsDebugExtApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * Yaml配置是否启用Debug功能
 *
 * @author Adamancy Zhang at 13:49 on 2024-05-25
 */
@Order(88)
@Component
@SPI.Service
public class ConfigureIsDebugExtApi implements IsDebugExtApi {

    @Value("${pamirs.framework.debug.enabled:true}")
    private boolean isEnabledDebug;

    @Override
    public boolean isDebug() {
        return isEnabledDebug;
    }
}
