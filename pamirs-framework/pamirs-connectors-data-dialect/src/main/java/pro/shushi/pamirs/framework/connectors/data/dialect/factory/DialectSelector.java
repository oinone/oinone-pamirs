package pro.shushi.pamirs.framework.connectors.data.dialect.factory;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DialectSelectorApi;
import pro.shushi.pamirs.framework.connectors.data.dialect.configuration.DsDialectConfiguration;
import pro.shushi.pamirs.meta.common.spi.SPI;

import jakarta.annotation.Resource;

/**
 * 方言服务选择器
 * <p>
 * 2020/7/16 1:48 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
public class DialectSelector implements DialectSelectorApi {

    @Resource
    private DsDialectConfiguration dsDialectConfiguration;

    @Override
    public String type(String dsKey) {
        DialectVersion dialectVersion = dsDialectConfiguration.dialectVersion(dsKey);
        return dialectVersion.getTypeAndVersion();
    }

    @Override
    public String major(String dsKey) {
        DialectVersion dialectVersion = dsDialectConfiguration.dialectVersion(dsKey);
        return dialectVersion.getTypeAndMajorVersion();
    }

}
