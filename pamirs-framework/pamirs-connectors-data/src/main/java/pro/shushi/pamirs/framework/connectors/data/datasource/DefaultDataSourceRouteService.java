package pro.shushi.pamirs.framework.connectors.data.datasource;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.framework.connectors.data.api.service.DataSourceRouteService;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Optional;

/**
 * 数据源路由实现
 * <p>
 * 2020/6/8 12:23 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
public class DefaultDataSourceRouteService implements DataSourceRouteService {

    @Override
    public Object route(String model) {
        return Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(model))
                .map(ModelConfig::getDsKey).orElse(null);
    }

}
