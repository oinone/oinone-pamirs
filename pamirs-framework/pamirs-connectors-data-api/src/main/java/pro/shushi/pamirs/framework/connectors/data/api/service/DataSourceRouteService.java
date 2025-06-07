package pro.shushi.pamirs.framework.connectors.data.api.service;

import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 路由服务API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SuppressWarnings("unused")
@SPI
public interface DataSourceRouteService {

    default Object route(String model) {
        return null;
    }

}
