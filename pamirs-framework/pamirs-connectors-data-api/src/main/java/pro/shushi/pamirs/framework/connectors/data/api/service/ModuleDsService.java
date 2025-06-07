package pro.shushi.pamirs.framework.connectors.data.api.service;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;

/**
 * 模块数据源映射API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SuppressWarnings("unused")
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ModuleDsService {

    default Map<String/*module*/, String/*dsKey*/> dsMap() {
        return null;
    }

    default Map<String/*model*/, String/*dsKey*/> modelDsMap() {
        return null;
    }

}
