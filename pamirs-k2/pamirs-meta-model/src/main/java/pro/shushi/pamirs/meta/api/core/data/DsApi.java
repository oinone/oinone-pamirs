package pro.shushi.pamirs.meta.api.core.data;

import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;

/**
 * 数据源接口SPI
 * <p>
 * 2020/11/10 3:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface DsApi {

    default boolean existDs(String model) {
        return true;
    }

    default String systemDsKey() {
        return null;
    }

    default String originSystemDsKey() {
        return null;
    }

    default String baseDsKey(String model) {
        return null;
    }

    default String defaultDsKey() {
        return null;
    }

    default String originDefaultDsKey() {
        return null;
    }

    default Map<String, String> fetchModuleDsMap() {
        return null;
    }

    default Map<String, String> fetchModelDsMap() {
        return null;
    }

    HoldKeeper<DsApi> holder = new HoldKeeper<>();

    static DsApi get() {
        return holder.supply(() -> Spider.getDefaultExtension(DsApi.class));
    }
}
