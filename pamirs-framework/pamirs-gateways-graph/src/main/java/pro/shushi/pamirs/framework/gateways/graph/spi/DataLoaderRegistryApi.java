package pro.shushi.pamirs.framework.gateways.graph.spi;

import org.dataloader.DataLoaderRegistry;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 数据加载器注册api
 * <p>
 * 2021/3/29 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface DataLoaderRegistryApi {

    String COMMON_DATA_LOADER = "commonDataLoader";

    DataLoaderRegistry dataLoader();

    HoldKeeper<DataLoaderRegistryApi> holder = new HoldKeeper<>();

    static DataLoaderRegistryApi get() {
        return holder.supply(() -> Spider.getDefaultExtension(DataLoaderRegistryApi.class));
    }
}
